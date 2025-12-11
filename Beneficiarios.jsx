import React, {useEffect, useState} from 'react'
import api from '../services/api'

export default function Beneficiarios(){
  const [list, setList] = useState([])
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState({nombre:'', edad:'', tipoServicio:''})

  async function load(){
    const res = await api.get('/api/beneficiarios')
    setList(res.data)
  }
  useEffect(()=>{ load() },[])

  function startCreate(){
    setEditing(null)
    setForm({nombre:'', edad:'', tipoServicio:''})
  }

  function startEdit(b){
    setEditing(b.id)
    setForm({nombre:b.nombre, edad:b.edad, tipoServicio:b.tipoServicio})
  }

  async function save(e){
    e.preventDefault()
    if(editing){
      await api.put('/api/beneficiarios/'+editing, form)
    } else {
      await api.post('/api/beneficiarios', form)
    }
    await load()
    setEditing(null)
  }

  async function remove(id){
    if(!confirm('Eliminar?')) return
    await api.delete('/api/beneficiarios/'+id)
    await load()
  }

  function exportExcel(){ window.open(api.defaults.baseURL + '/api/beneficiarios/export/excel' , '_blank') }
  function exportPdf(){ window.open(api.defaults.baseURL + '/api/beneficiarios/export/pdf' , '_blank') }

  return (
    <div>
      <div style={{display:'flex', justifyContent:'space-between', marginBottom:10}}>
        <h2>Beneficiarios</h2>
        <div>
          <button onClick={startCreate}>Nuevo</button>{' '}
          <button onClick={exportExcel}>Exportar Excel</button>{' '}
          <button onClick={exportPdf}>Exportar PDF</button>
        </div>
      </div>

      <table border="1" cellPadding="6" style={{width:'100%', borderCollapse:'collapse'}}>
        <thead>
          <tr><th>ID</th><th>Nombre</th><th>Edad</th><th>Tipo Servicio</th><th>Acciones</th></tr>
        </thead>
        <tbody>
          {list.map(b=>(
            <tr key={b.id}>
              <td>{b.id}</td>
              <td>{b.nombre}</td>
              <td>{b.edad}</td>
              <td>{b.tipoServicio}</td>
              <td>
                <button onClick={()=>startEdit(b)}>Editar</button>{' '}
                <button onClick={()=>remove(b.id)}>Eliminar</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <div style={{marginTop:20}}>
        <h3>{editing ? 'Editar' : 'Nuevo'}</h3>
        <form onSubmit={save}>
          <div>
            <label>Nombre</label><br/>
            <input value={form.nombre} onChange={e=>setForm({...form, nombre:e.target.value})} required/>
          </div>
          <div>
            <label>Edad</label><br/>
            <input type="number" value={form.edad} onChange={e=>setForm({...form, edad:e.target.value})} required/>
          </div>
          <div>
            <label>Tipo Servicio</label><br/>
            <input value={form.tipoServicio} onChange={e=>setForm({...form, tipoServicio:e.target.value})} required/>
          </div>
          <div style={{marginTop:10}}>
            <button type="submit">Guardar</button>
            <button type="button" onClick={()=>{setEditing(null); setForm({nombre:'',edad:'',tipoServicio:''})}}>Cancelar</button>
          </div>
        </form>
      </div>
    </div>
  )
}
