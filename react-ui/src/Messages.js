import React from 'react'

const Messages = ({ messages, currentUser }) => {

    let renderMessage = (message) => {
        const { sender, content, color } = message;
        const messageFromMe = currentUser.username === message.sender;
        const className = messageFromMe ? "Messages-message currentUser" : "Messages-message";
    
        if(messageFromMe){
        return (
            <div class="d-flex justify-content-end mb-4">
            <div class="msg_cotainer_send">
            {message.content}
                <span class="msg_time_send">{message.sender}</span>
            </div>
            </div>
        );
        }else{
            return (
            <div class="d-flex justify-content-start mb-4">
            <div class="msg_cotainer">
            {message.content}
              <span class="msg_time">{message.sender}</span>
            </div>
            </div>
            )
        }
    };

    return (
        <>
            {messages.map(msg => renderMessage(msg))}
        </>
    )
}


export default Messages