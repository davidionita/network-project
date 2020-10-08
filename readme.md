**network-project=1**

(Lab 1 - Better Basic Chat App.: Sprint #1)

Trello Board: https://trello.com/b/jbDSqjlH/network-project-1

Schoology Assignment: https://bca.schoology.com/assignment/3098480746/info

**Documentation**

***Client***

****Name****

When joining the chat server, you must provide a valid name before chatting.

A valid name must meet the following criteria:
- Nonempty
- Only alphanumeric characters
- No whitespace

To set your name after joining the chat server, respond with `[your desired name]` when prompted by the dialogue.

_Note:_ You may not use any other client command (below) until you set a valid name.

****Chatting****

To send a message to the entire server, respond with `[your desired message]`.

To (privately) send a message to a specific user, respond with `@[username] [your desired private message]`.

****Quiting****

To quit the chat server, respond with `/quit` at any time.

***FileLogger***

In addition to the updates above, there is now a much more advanced logging system on the server and client.

The client will log, to a separate file, most interactions and include a timestamp for each log file. 

This will help improve debugging and security.

A full list of log types and descriptions can be found below:
- Connected - the client connected to server
- Disconnected - the client disconnected from server
- Info - general information logging
- Error - a general error has occurred
- Prompt - the server has prompted a response
- Received Packet - a packet has been received by the client
- Sent Packet - a packet has been sent by the client
- Packet Error - a packet error has occurred
- Chat - a chat has occurred
- User Input - the client typed user input
- Invalid Command - the client inputted an invalid command

Each different type is logged to the file with a custom prefix, helping make each event clear to administrators.