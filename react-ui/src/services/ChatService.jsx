import axios from 'axios';

const EMPLOYEE_API_BASE_URL = "http://localhost:8081/";

class ChatService {

    sendMessage(data){
        return axios.post(EMPLOYEE_API_BASE_URL+'chat/sendMessage', data);
    }

}

export default new ChatService()