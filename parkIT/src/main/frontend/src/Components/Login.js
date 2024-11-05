import React, { Component, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { useDispatch, useSelector } from 'react-redux';
import { changeToken } from '../store';

function Login(props) {

  const navigate = useNavigate();

  const [id, setId] = useState("");
  const [pw, setPw] = useState("");

  const changeId = (event) => {
    setId(event.target.value);
  }
  const changePw = (event) => {
    setPw(event.target.value);
  }

  let token = useSelector((state) => state.accessToken);
  let dispatch = useDispatch();

  useEffect(() => {
    if(token.JWT) {
      alert("AccessToken: " + token.JWT);
    }
  }, [token.JWT]);

  const login = async () => {

    const req = {
      id: id,
      pw: pw
    }
    const url = `https://localhost:3000/api/${props.user}/login`;

    await axios.post(url, req)
      .then((resp) => {
        console.log("[Login.js] login() success :D");
        console.log("AccessToken: " + resp.headers.authorization);

        //redux로 상태관리
        dispatch(changeToken(resp.headers.authorization));

        // alert("AccessToken: " + token.JWT);
      }).catch((err) => {
        console.log("[Login.js] login() fail :(");
        alert(err);
      });

  }

  return (
    <div>
      <h3> 로그인 페이지 </h3>
      <table className="table">
        <tbody>
          <tr>
            <th>아이디: </th>
            <td>
              <input type="text" value={id} onChange={changeId}/>
            </td>
          </tr>
          <tr>
            <th>비밀번호: </th>
            <td>
              <input type="password" value={pw} onChange={changePw}/>
            </td>
          </tr>
        </tbody>
      </table>
      <br/>

      <div>
        <button onClick={login}> 로그인 </button>
      </div>
      <br/><br/>
    </div>
  );
}

export default Login;