import axios from "axios";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

function MemberJoin() {

  const [id, setId] = useState("");
  const [pw, setPw] = useState("");
  const [pwConfirm, setPwConfirm] = useState("");
  const [name, setName] = useState("");
  const [tel, setTel] = useState("");
  const [birth, setBirth] = useState("");
  const [email, setEmail] = useState("");
 // const [reg, setReg] = useState("");

  const navigate = useNavigate();

  const changeId = (event) => {
    setId(event.target.value);
  }
  const changePw = (event) => {
    setPw(event.target.value);
  }
  const changePwConfirm = (event) => {
    setPwConfirm(event.target.value);
  }
  const changeName = (event) => {
    setName(event.target.value);
  }
  const changeTel = (event) => {
    setTel(event.target.value);
  }
  const changeBirth = (event) => {
    setBirth(event.target.value);
  }
  const changeEmail = (event) => {
    setEmail(event.target.value);
  }

  /* 아이디 중복췍 */
  const checkId = async () => {

    await axios.get("https://localhost:3000/api/member/find", {params: {id:id}})
      .then((resp) => {
        console.log("[MemberJoin.js] checkId() success :D");
        alert(resp.data);
      }).catch((err) => {
        console.log("[MemberJoin.js] checkId() fail :(");
        alert(err.response.status);
      });

  }
  /* 아이디 중복췍 */

  /* 회원가입 */
  const join = async () => {
    
    const today = new Date();
    const year = today.getFullYear();
    const month = ('0' + (today.getMonth() + 1)).slice(-2);
    const date = ('0' + today.getDate()).slice(-2);
    const reg = year + "-" + month + "-" + date;

    const req = {
      id: id,
      pw: pw,
      pwConfirm: pwConfirm,
      name: name,
      tel: tel,
      birth: birth,
      email: email,
      reg: reg
    }
    const config = {"Content-Type": 'application/json'};

    await axios.post("https://localhost:3000/api/member/join", req, config)
      .then((resp) => {
        console.log("[MemberJoin.js] join() success :D");
        console.log(resp.data.id + " 회원가입 성공");
        alert(resp.data);
      }).catch((err) => {
        console.log("[MemberJoin.js] join() fail :<");
        alert(err);
      });

  }

  return (
    <div>
      <h3> 사용자 회원가입 페이지 </h3>
      <table className="table">
        <tbody>
          <tr>
            <th>아이디</th>
            <td>
              <input type="text" value={id} onChange={changeId}/> &nbsp; &nbsp;
              <button onClick={checkId}>중복확인</button>
            </td>
          </tr>
          <tr>
            <th>비밀번호</th>
            <td>
              <input type="password" value={pw} onChange={changePw}/>
            </td>
          </tr>
          <tr>
            <th>비밀번호 확인</th>
            <td>
              <input type="password" value={pwConfirm} onChange={changePwConfirm}/>
            </td>
          </tr>
          <tr>
            <th>이름</th>
            <td>
              <input type="text" value={name} onChange={changeName}/>
            </td>
          </tr>
          <tr>
            <th>전화번호</th>
            <td>
              <input type="text" value={tel} onChange={changeTel}/>
            </td>
          </tr>
          <tr>
            <th>생년월일</th>
            <td>
              <input type="date" value={birth} onChange={changeBirth}/>
            </td>
          </tr>
          <tr>
            <th>이메일</th>
            <td>
              <input type="text" value={email} onChange={changeEmail}/>
            </td>
          </tr>
        </tbody>
      </table>
      <br/>

      <div>
        <button onClick={join}>회원가입</button>
      </div>

    </div>
  );
}

export default MemberJoin;