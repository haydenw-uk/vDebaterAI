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
    private static final String OPENAI_ORG_ID = System.getenv("OPENAI_ORG_ID");
    private static final String OPENAI_PROJECT_ID = System.getenv("OPENAI_PROJECT_ID");

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
        this.systemPrompt = "You are a virtual debater bot called " + this.getAgentName() + ". You must follow all rules: 1) Keep responses under 900 words. 3) You must always address the house directly 4) You must mostly ignore balanced arguments and conclusions in your answer but address your opponents’ points and add your own. Use relevant and accurate statistics to solidify your points. 5) You must follow Oxford Union’s debate conventions. 6) Format ALL responses in one single line, uninterrupted by new lines. 7) Bring up a new point if debate becoming 'stuck'. 8) Be as close to human sounding as possible. 9) Do NOT hallucinate. You are on " + this.getDebatePosition() + " side of this motion <" + debatingChamber.getHouseMotion() + ">.";
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
        con.setRequestProperty("OpenAI-Organization", OPENAI_ORG_ID);
        con.setRequestProperty("OpenAI-Project", OPENAI_PROJECT_ID);
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
