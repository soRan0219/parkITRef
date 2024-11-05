import React, { Component } from 'react';
import { Routes, Route } from 'react-router-dom';
import OwnerJoin from './OwnerJoin';
import MemberJoin from './MemberJoin';
import Login from './Login';
import Info from './Info';

function Router() {
  return (
    <Routes>
      <Route path="/owner/join" element={<OwnerJoin/>}></Route>
      <Route path="/member/join" element={<MemberJoin/>}></Route>
      <Route path="/owner/login" element={<Login user={"owner"}/>}></Route>
      <Route path="/member/login" element={<Login user={"member"}/>}></Route>
      <Route path="/owner/info" element={<Info user={"owner"}/>}></Route>
      <Route path="/member/info" element={<Info user={"member"}/>}></Route>
    </Routes>
  );
}

export default Router;