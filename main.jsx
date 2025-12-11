import React from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import App from './App'
import Login from './pages/Login'
import Beneficiarios from './pages/Beneficiarios'

import './styles.css'

function Protected({children}) {
  const token = localStorage.getItem('token')
  return token ? children : <Navigate to="/login" replace />
}

createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login/>}/>
        <Route path="/" element={<Protected><App/></Protected>}>
          <Route index element={<Beneficiarios/>}/>
        </Route>
      </Routes>
    </BrowserRouter>
  </React.StrictMode>
)
