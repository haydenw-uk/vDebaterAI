import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javazoom.jl.player.Player;
import java.io.FileInputStream;

public class Audio {
    private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");
    private static final String TTS_API_URL = System.getenv("OPENAI_TTS_MODELS_API_URL");

    public static void generateTTSAudioAndPlay(String text, String voice) {
        try {
            URL url = new URL(TTS_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + OPENAI_API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonPayload = "{\"model\": \"tts-1\", \"input\": \"" + text + "\", \"voice\": \"" + voice + "\"}";
            conn.getOutputStream().write(jsonPayload.getBytes());

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                byte[] audioBytes = conn.getInputStream().readAllBytes();
                Files.write(Paths.get("speech.mp3"), audioBytes);
            } else {
                System.out.println("[ERROR] Error generating audio file. Response code: " + conn.getResponseCode());
            }

            conn.disconnect();
            Audio.playAudio("speech.mp3");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void playAudio(String filePath) throws Exception {
        FileInputStream fis = new FileInputStream(filePath);
        Player player = new Player(fis);
        player.play();
    }
}