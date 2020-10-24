**network-project=1**

(Lab 1 - Better Basic Chat App.: Sprint #1)
(Lab 2 - Better Basic Chat App.: Sprint #2)

Trello Board: https://trello.com/b/jbDSqjlH/network-project-1

**Documentation**

***Client***

****Name****

GUI and Text: 

When joining the chat server, you must provide a valid name before chatting.

A valid name must meet the following criteria:
- Nonempty
- Only alphanumeric characters
- No whitespace

To set your name after joining the chat server, respond with `[your desired name]` when prompted by the dialogue.

_Note:_ You may not use any other client command (below) until you set a valid name.

****Chatting****

GUI and Text: 

To send a message to the entire server, respond with `[your desired message]`.

To privately send a message to a specific user, respond with `@[username] [your desired private message]`.

You may also privately send a message to multiple users, just add subsequent `@[username]`s seperated by spaces between the first `@[username]` and message.

****Quiting****

GUI: To quit the chat server, just exit your GUI window at any time. 
Text: To quit the chat server, respond with `/quit` at any time.

****Current Users****

GUI: The current users connected to the server are always displayed at the top of the window and are continuously updated as users join/disconnect.
Text: You will be sent a list of current users connected to the server when you first enter the server.  If you would like to check the current users, send the command `\whoishere` at any time.

***Emoji Keyboard***

The emoji keyboard button is exclusive to the GUI client.  When pressing this button, your Operating System's default emoji keyboard will open, allowing you to type and send emojis through the chat.  

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