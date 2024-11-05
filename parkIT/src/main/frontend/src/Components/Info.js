import React, { Component, useEffect, useState } from 'react';
import { redirect, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { useDispatch, useSelector } from 'react-redux';
import { changeToken } from '../store';

function Info(props) {

  const navigate = useNavigate();

  const [id, setId] = useState("");
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [tel, setTel] = useState("");

  let accessToken = useSelector((state) => state.accessToken);
  let dispatch = useDispatch();

  useEffect(() => {
    if(accessToken.JWT) {
      console.log("AccessToken 상태변경: " + accessToken.JWT);
      // sendAccessToken(accessToken.JWT);
    }
  }, [accessToken.JWT]);

  //const getInfo = async () => {
  useEffect(() => {

    const url = `https://localhost:3000/api/${props.user}/info`;

    //accessToken 보내기
    const sendAccessToken = (token) => {
      console.log("sendAccessToken(): " + token);
      return axios.get(url, {
        headers: {Authorization: "Bearer" + token}
      }).then((resp) => {
        setId(resp.data.id);
        setName(resp.data.name);
        setEmail(resp.data.email);
        setTel(resp.data.tel);
      }).catch((err) => {
        throw new Error(" [Info.js] sendAccessToken() failed.");
      });
    };


    console.log("send accessToken: " + accessToken.JWT);
    axios.get(url, {
      headers: {Authorization: "Bearer" + accessToken.JWT}
    }).then((resp) => {
      if(resp.headers.authorization) {
        const reissuedToken = resp.headers.authorization;
        console.log("reissued AccessToken: " + reissuedToken);

        dispatch(changeToken(reissuedToken));

        sendAccessToken(reissuedToken);
        // return reissuedToken;
      } else {
        setId(resp.data.id);
        setName(resp.data.name);
        setEmail(resp.data.email);
        setTel(resp.data.tel);
      }
    }).catch((err) => {
      console.log(" [Info.js] failed.");
      if(err.response && err.response.status===401) {
        alert("다시 로그인 해주세요.");
      } else {
        alert(err);
      }
    });

  }, []);

  return (
    <div>
      <h3> 나의 정보 </h3>
      <table className="table">
        <tbody>
          <tr>
            <th>아이디: </th>
            <td> {id} </td>
          </tr>
          <tr>
            <th>이름: </th>
            <td> {name} </td>
          </tr>
          <tr>
            <th>이메일: </th>
            <td> {email} </td>
          </tr>
          <tr>
            <th>전화번호: </th>
            <td> {tel} </td>
          </tr>
        </tbody>
      </table>
    </div>
  );
}

export default Info;