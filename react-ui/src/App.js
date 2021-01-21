import logo from './logo.svg';
import './App.css';
import { Component } from 'react';
import $ from 'jquery';
import SockJsClient from 'react-stomp';
import ChatService from './services/ChatService';
import Messages from './Messages';

class App extends Component {
  constructor(props){
    super(props)
    this.state={
      username:{},
      messages:[]
    }
    this.registerUserName=this.registerUserName.bind(this)
    this.submitMessage=this.submitMessage.bind(this)
    this.onConnected = this.onConnected.bind(this)
    this.onMessageReceived = this.onMessageReceived.bind(this)
    this.renderMessage=this.renderMessage.bind(this)
  }

  onConnected ()  {
    console.log("Connected!!")
  }

  onMessageReceived  (msg) {
    console.log('New Message Received!!', msg);
    var messagesAll = this.state.messages
    messagesAll = messagesAll.concat(msg)
    this.setState({messages:messagesAll})
    //setMessages(messages.concat(msg));
  }

  registerUserName(){
    //var usernameVal = $('#username').val()
    var userNameDetails = {}
    userNameDetails.username=$('#username').val()
    this.setState({username:userNameDetails})
  }

  submitMessage(){
    var data={}
    data.content=$('#message').val()
    data.sender=this.state.username.username
    ChatService.sendMessage(data).then(res => {
      console.log('Sent', res);
      $('#message').val("")
    }).catch(err => {
      console.log('Error Occured while sending message');
    })
  }

  renderMessage = (message) => {
    if(this.state.username.username==message.sender){
      return '<div class="d-flex justify-content-start mb-4">'+
            '<div class="msg_cotainer">'+
            message.content+
            '<span class="msg_time">'+message.sender+'</span>'
            +'</div>';
    }
            
  }

  
  render (){
      return (
        <div class="container-fluid h-100" style={{paddingTop:41}}>
          <div class="row">
          <div class="col-md-8 col-xl-3 chat"></div>
				<div class="col-md-8 col-xl-6 chat">
					<div class="card">
        {!this.state.username.username?
        <div class="row" style={{paddingTop:41}}>
          <div class='col-sm-3'></div>
          <div class='col-sm-6'>
        <div class="form-group" style={{width:300}}>
        <label for="Username" class="sr-only">Username</label>
        <input type="text" class="form-control" id="username" placeholder="Username"/>
      </div>
      <button onClick={this.registerUserName} style={{width:100}} class="btn btn-primary ">Login</button>
      </div>
      <div class='col-sm-3'></div>
      </div>
        :
        <>
        <SockJsClient
              url={"http://localhost:8081"}
              topics={['/topic/group']}
              onConnect={this.onConnected}
              onDisconnect={console.log("Disconnected!")}
              onMessage={msg => this.onMessageReceived(msg)}
              debug={false}
            />
      
        <div class="card-body msg_card_body">
        
        <Messages
        messages={this.state.messages}
        currentUser={this.state.username}
      />

        </div>


        <div class="card-footer">
							<div class="input-group">
								<div class="input-group-append">
									<span class="input-group-text attach_btn"><i class="fas fa-paperclip"></i></span>
								</div>
								<textarea id="message" name="" class="form-control type_msg" placeholder="Type your message..."></textarea>
								<div onClick={this.submitMessage} class="input-group-append">
									<span class="input-group-text send_btn"><i class="fas fa-location-arrow"></i></span>
								</div>
							</div>
					</div>

        </>
        }
        
      </div>
      </div>
      <div class="col-md-8 col-xl-3 chat"></div>
      </div>
      </div>
    )
    }
}

export default App;
