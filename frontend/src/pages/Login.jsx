import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Link, useNavigate } from 'react-router-dom';
import { loginUser, clearError } from '../redux/slices/authSlice';
import Button from '../components/Button';
import Input from '../components/Input';

const Login = () => {
  const [formData, setFormData] = useState({ email: '', password: '' });
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { isLoading, error } = useSelector((state) => state.auth);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    if (error) dispatch(clearError());
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const result = await dispatch(loginUser(formData));
    if (loginUser.fulfilled.match(result)) {
      navigate('/');
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 p-4">
      <div className="w-full max-w-[350px] space-y-4">
        {/* Main Card */}
        <div className="bg-white border border-gray-300 p-8 md:p-10 flex flex-col items-center">
          <h1 className="text-4xl font-serif font-bold mb-8 tracking-wide">ChatterBox</h1>

          {error && (
            <div className="w-full mb-4 text-center">
              <p className="text-sm text-red-500">{error}</p>
            </div>
          )}

          <form onSubmit={handleSubmit} className="w-full space-y-2">
            <div className="mb-2">
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                placeholder="Email"
                required
                className="w-full px-2 py-2.5 bg-gray-50 border border-gray-300 rounded-sm text-xs focus:outline-none focus:border-gray-400"
              />
            </div>
            <div className="mb-4">
              <input
                type="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                placeholder="Password"
                required
                className="w-full px-2 py-2.5 bg-gray-50 border border-gray-300 rounded-sm text-xs focus:outline-none focus:border-gray-400"
              />
            </div>

            <Button
              type="submit"
              className="w-full py-1.5 text-sm font-semibold rounded-[4px] bg-blue-500 hover:bg-blue-600 text-white shadow-none"
              isLoading={isLoading}
            >
              Log in
            </Button>
          </form>

          <div className="flex items-center w-full my-5">
            <div className="h-px bg-gray-300 flex-1"></div>
            <span className="px-4 text-xs text-gray-500 font-semibold uppercase">OR</span>
            <div className="h-px bg-gray-300 flex-1"></div>
          </div>

          <button className="text-xs text-blue-900 mt-4">
            Forgot password?
          </button>
        </div>

        {/* Sign up link */}
        <div className="bg-white border border-gray-300 p-5 text-center">
          <p className="text-sm">
            Don't have an account?{' '}
            <Link to="/signup" className="font-semibold text-blue-500">
              Sign up
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;
