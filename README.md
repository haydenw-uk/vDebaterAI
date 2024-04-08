# Virtual Debater AI Simulator

This is a Java application that simulates a virtual debate between autonomous AI agents representing opposing debate positions,
based on the Oxford Union's rules and conventions of debate.

The application allows the user to enter a motion for debate, set up a debate chamber, and configure the debate participants. 
The AI agents will then debate the motion and generate text-to-speech audio responses.

## Features

- Allow the user to enter a motion for debate
- Set up a debate chamber with a president, proposition debaters, and opposition debaters
- Generate text-to-speech audio responses for each AI agent's speech
- Record the debate minutes in a text file

## Prerequisites

- Built with *openjdk version "19.0.2" 2023-01-17* - this version or later advised
- *google.code.gson*
- *googlecode.soundlibs.jlayer*
- Correctly configured *.env* file (see below)

### NOTE - .env needs to be created

*.env MUST contain...*
- OPENAI_API_KEY=PROVIDE-YOUR-OWN-FROM-OPEN-AI
- OPENAI_TTS_MODELS_API_URL=https://api.openai.com/v1/audio/speech
- OPENAI_LLM_MODELS_API_URL=https://api.openai.com/v1/chat/completions


## Future Improvements (TODO)

- [ ] Implement audience members who can raise points of information and vote on the motion
- [ ] Integrate support for further variety of Large Language Models (e.g., Claude 3 or GPT-4) for better AI responses
- [ ] Add support for live fact-checking using an internet-connected model such as Perplexity.AI's API or Exa.AI.
- [ ] Implement a graphical user interface for a more user-friendly experience


### DISCLAIMER : This project is not affiliated to the Oxford Union, but is heavily based on rules and conventions followed during the famous Oxford Union Formal Debates on Thursdays during tern-time.
