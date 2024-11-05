import axios from "axios";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

function OwnerJoin() {

  const [id, setId] = useState("");
  const [pw, setPw] = useState("");
  const [pwConfirm, setPwConfirm] = useState("");
  const [name, setName] = useState("");
  const [tel, setTel] = useState("");
  const [email, setEmail] = useState("");

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
  const changeEmail = (event) => {
    setEmail(event.target.value);
  }

  /* 아이디 중복췍 */
  const checkId = async () => {

    await axios.get("https://localhost:3000/api/owner/find", {params: {id:id}})
      .then((resp) => {
        console.log("[OwnerJoin.js] checkId() success :D");
        alert(resp.data);
      }).catch((err) => {
        console.log("[OwnerJoin.js] checkId() fail :(");
        alert(err.response.status);
      });

  }
  /* 아이디 중복췍 */

  /* 회원가입 */
  const join = async () => {
    
    const req = {
      id: id,
      pw: pw,
      pwConfirm: pwConfirm,
      name: name,
      tel: tel,
      email: email
    }
    const config = {"Content-Type": 'application/json'};

    await axios.post("https://localhost:3000/api/owner/join", req, config)
      .then((resp) => {
        console.log("[OwnerJoin.js] join() success :D");
        console.log(resp.data.id + " 회원가입 성공");
        alert(resp.data);
      }).catch((err) => {
        console.log("[OwnerJoin.js] join() fail :<");
        alert(err);
      });

  }

  return (
    <div>
      <h3> 점주 회원가입 페이지 </h3>
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

export default OwnerJoin;