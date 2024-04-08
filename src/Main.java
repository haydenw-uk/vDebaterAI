import java.util.ArrayList;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String orgName = "AI Debater Academy";

        // Configure input scanner object for user input
        Scanner kb = new Scanner(System.in);

        welcomeScreenForUser();

        /// Ask user for Motion, Setup House for Debate with Motion
        String motion = getMotionFromUser();
        Chamber debatingChamber = new Chamber(motion);


        // President's initialisation with user entered name
        // Ask for President's Name
        System.out.println("Enter The President's Name : ");
        String presidentNameUserInput = kb.nextLine();

        // Create Agent with President role
        Agent president = new Agent(presidentNameUserInput, Agent.debatePosition.NEUTRAL, debatingChamber, "fable");
        debatingChamber.AddEntityToChamber(president);


        // Proposition Debater's initialisation with user entered name
        // Create Agent with Proposition Debater role
        Agent propositionDebaterOne = new Agent("Rachael Rhonden", Agent.debatePosition.PROPOSITION, debatingChamber, "alloy");
        debatingChamber.AddEntityToChamber(propositionDebaterOne);


        // Opposition Debater's initialisation with user entered name
        // Create Agent with Opposition Debater role
        Agent oppositionDebaterOne = new Agent("Sam Bloggs", Agent.debatePosition.OPPOSITION, debatingChamber, "echo");
        debatingChamber.AddEntityToChamber(oppositionDebaterOne);



        // Proposition Debater's initialisation with user entered name
        // Create Agent with Proposition Debater role
        Agent propositionDebaterTwo = new Agent("Sam Christies", Agent.debatePosition.PROPOSITION, debatingChamber, "onyx");
        debatingChamber.AddEntityToChamber(propositionDebaterTwo);


        // Opposition Debater's initialisation with user entered name
        // Create Agent with Opposition Debater role
        Agent oppositionDebaterTwo = new Agent("Samantha Jarvies", Agent.debatePosition.OPPOSITION, debatingChamber, "shimmer");
        debatingChamber.AddEntityToChamber(oppositionDebaterTwo);





        // Wipe, Prepare 'Minutes' Text File
        Minutes.clearFile();
        Minutes.appendMinutes("Debate Minutes (" + orgName + ") " + "- " + debatingChamber.getHouseMotion().toUpperCase());

        // Output House Debate Banner
        System.out.println(orgName.toUpperCase() + " - " + debatingChamber.getHouseMotion().toUpperCase() + "\n" + "---");
        debatingChamber.setDebateRunning(true);

        // Output president's welcome / introduction to the debate and Motion (hardcoded)
        String presidentWelcomeText = "Welcome to " + orgName + "'s Virtual AI Formal Debate. I am " + presidentNameUserInput + ", your president. Today we are debating the Motion: " + debatingChamber.getHouseMotion() + ": I look to the Proposition to open this motion...";
        Audio.generateTTSAudioAndPlay(presidentWelcomeText, "fable");

        // Preposition opens debate
        Agent.debatePosition currentSideSeekingForSpeaker = Agent.debatePosition.PROPOSITION;

        while (debatingChamber.isDebateRunning())
        {
            // While the debate is running
            for(Agent currentIndexAgent : debatingChamber.getAllEntitiesPresent())
            {
                // Iterate over each Agent present in chamber until the current side speaker being seeked is reached
                if(currentIndexAgent.getDebatePosition() == currentSideSeekingForSpeaker)
                {
                    // Set agent to speaker via executive authority
                    Agent speakingAgent = currentIndexAgent;
                    speakingAgent.setCurrentSpeakerPrivilege(true);

                    // Format, output speaker information text to denote current agent speaker and its side of debate
                    String formattedSpeaker = speakingAgent.getAgentName() + " (" + speakingAgent.getDebatePosition().toString() + ") : ";
                    System.out.print(formattedSpeaker);

                    // Generate, save AI agent's response with LLM (parsing context of Agent System Prompt and Debate Minutes)
                    String response = speakingAgent.getAIAgentResponse("gpt-3.5-turbo", speakingAgent.getSystemPrompt(), Minutes.readMinutes());

                    // Generate, Play Text-to-Speech File of AI agent response
                    Audio.generateTTSAudioAndPlay(response, speakingAgent.getAgentTTSVoice());
                    System.out.print(response);

                    // Important 'blank' line for formatting's sake in console
                    System.out.println();

                    // Set agent to non-speaker via executive authority
                    speakingAgent.setCurrentSpeakerPrivilege(false);

                    // Append last AI agent's response text into debate 'minutes'
                    Minutes.appendMinutes("~\n" + formattedSpeaker + response);

                    // Change debate side to opposite to most recent speaker to allow for next speaker on opposing side to respond
                    if(currentSideSeekingForSpeaker == Agent.debatePosition.PROPOSITION)
                    {
                        currentSideSeekingForSpeaker = Agent.debatePosition.OPPOSITION;
                    }
                    else
                    {
                        currentSideSeekingForSpeaker = Agent.debatePosition.PROPOSITION;
                    }

                }

            }

        }


        // TODO - Implement Audience Members
        /// Roles: Can raise POIs, Can vote on Motions 'Ay' OR 'Nay' at the end
        // Notes: Different LLMs (try and get Internet-connected model like Perplexity for live fact-checking)

    }


    public static String getMotionFromUser()
    {
        System.out.println("Enter your motion: ");
        Scanner kb = new Scanner(System.in);

        return kb.nextLine();
    }
    public static void welcomeScreenForUser()
    {
        System.out.println("Welcome to the Virtual Debater AI Simulator");
        System.out.println("*** PLEASE NOTE, THIS BETA IMPLEMENTATION WILL NOT SELF-TERMINATE!***");
    }

    public static void clearConsole(int numerOflinesToClear)
    {
        // Print empty lines to clear the console
        for (int i = 0; i < numerOflinesToClear; i++) {
            System.out.println();
        }
    }

}