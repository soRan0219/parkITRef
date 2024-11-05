import {useEffect, useState} from "react";
import axios from "axios";
import { BrowserRouter, Link } from "react-router-dom";
import Main from "./Components/Main";

function App() {
  // const [hello, setHello] = useState('');

//  useEffect(() => {
//    axios.get('/api/test')
//      .then((res) => {
//        setHello(res.data);
//      })
//  }, []);

  return (
    /*
    <div className="App">
      백엔드 데이터 : {hello}
    </div>
    */
    <div>
      <BrowserRouter>
        <Main/>
        <Link to="/owner/join"> 점주 회원가입 </Link>
        <br/>
        <Link to="/member/join"> 사용자 회원가입 </Link>
        <br/>
        <Link to="/owner/login"> 점주 로그인 </Link>
        <br/>
        <Link to="/member/login"> 사용자 로그인 </Link>
        <br/>
        <Link to="/owner/info"> 점주 정보 </Link>
        <br/>
        <Link to="/member/info"> 사용자 정보 </Link>
      </BrowserRouter>
    </div>

  );
}

export default App;
