import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Agent {
    private String systemPrompt;
    private String agentName;
    private DebatePosition side;
    private boolean currentSpeakerPrivilege;
    private Chamber debatingChamber;

    private String ttsVoiceName;

    private static final String OPENAI_API_URL = System.getenv("OPENAI_LLM_MODELS_API_URL");
    private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");

    public enum DebatePosition {
        PROPOSITION,
        OPPOSITION,
        NEUTRAL
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public DebatePosition getDebatePosition()
    {
        return side;
    }

    public void setDebatePosition(DebatePosition sideToSet)
    {
        this.side = sideToSet;
    }

    public String getAgentName() {
        return agentName;
    }

    public String getAgentTTSVoice()
    {
        return this.ttsVoiceName;
    }

    public Agent(String agentName, DebatePosition agentSide, Chamber debatingChamber, String voiceForTTS)
    {
        this.agentName = agentName;
        this.side = agentSide;
        this.currentSpeakerPrivilege = false;
        this.systemPrompt = "You are a virtual debater bot called " + this.getAgentName() + ". You must follow all rules of the House: 1) Keep responses under 5 minutes when spoken 3) You must address the house vs your opposition directly 4) You must mostly ignore balanced arguments and conclusions in your answer but address your opponents’ points and add your own. Use facts and stats to back up answers. 5) You must follow Oxford Union’s conventions. YOUR RESPONSES MUST CONTAIN NO NEW LINES AND BE ONE BLOCK OF TEXT. DO NOT INCLUDE YOUR NAME IN OUTPUT, ONLY YOUR RESPONSE. Bring up a new point if debate becoming 'stuck'. Be as close to human sounding as possible. You are on the " + this.getDebatePosition() + " side of this motion <" + debatingChamber.getHouseMotion() + ">.";
        this.ttsVoiceName = voiceForTTS;

    }

    public boolean isCurrentSpeakerPrivilege() {
        return currentSpeakerPrivilege;
    }

    public void setCurrentSpeakerPrivilege(boolean currentSpeakerPrivilege) {
        this.currentSpeakerPrivilege = currentSpeakerPrivilege;
    }

    public String getAIAgentResponse(String llmName, String systemPrompt, String pastDebateMinutesAsString) throws IOException {
        URL url = new URL(OPENAI_API_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Bearer " + OPENAI_API_KEY);
        con.setDoOutput(true);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
        messages.addAll(parseMinutesAsMessages(pastDebateMinutesAsString));

        JsonArray jsonMessages = new JsonArray();
        for (Map<String, String> message : messages) {
            JsonObject jsonMessage = new JsonObject();
            jsonMessage.addProperty("role", message.get("role"));
            jsonMessage.addProperty("content", message.get("content"));
            jsonMessages.add(jsonMessage);
        }

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", llmName);
        requestBody.add("messages", jsonMessages);

        byte[] requestBodyBytes = requestBody.toString().getBytes(StandardCharsets.UTF_8);
        con.setRequestProperty("Content-Length", String.valueOf(requestBodyBytes.length));

        try (OutputStream os = con.getOutputStream()) {
            os.write(requestBodyBytes);
        }

        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonArray choices = jsonResponse.getAsJsonArray("choices");
            if (!choices.isEmpty()) {
                return choices.get(0).getAsJsonObject().getAsJsonObject("message").get("content").getAsString();
            }
        }
        return "Error: " + responseCode;
    }

    private List<Map<String, String>> parseMinutesAsMessages(String pastDebateMinutesAsString) {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", pastDebateMinutesAsString));
        return messages;
    }










}
