import { useState } from 'react';
import { motion } from 'framer-motion';

export default function Register() {
  const [form, setForm] = useState({ username: '', password: '' });
  const [msg, setMsg] = useState('');

  const handleChange = e => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async e => {
    e.preventDefault();
    const res = await fetch('/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form)
    });
    setMsg(res.ok ? 'Registered! Please log in.' : 'Registration failed.');
  };

  return (
    <div className="flex items-center justify-center h-screen bg-gray-100">
      <motion.form
        initial={{ y: -50, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ duration: 0.5 }}
        onSubmit={handleSubmit}
        className="bg-white p-8 rounded-lg shadow-lg w-80"
      >
        <h2 className="text-2xl font-bold mb-4">Register</h2>
        <input
          name="username"
          type="text"
          placeholder="Username"
          value={form.username}
          onChange={handleChange}
          className="w-full mb-3 p-2 border rounded"
          required
        />
        <input
          name="password"
          type="password"
          placeholder="Password"
          value={form.password}
          onChange={handleChange}
          className="w-full mb-4 p-2 border rounded"
          required
        />
        <button
          type="submit"
          className="w-full py-2 bg-indigo-600 text-white rounded hover:bg-indigo-700"
        >
          Sign Up
        </button>
        {msg && <p className="mt-3 text-center text-sm">{msg}</p>}
      </motion.form>
    </div>
  );
}
