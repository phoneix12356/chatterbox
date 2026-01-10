import React, { useState, useRef } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Link, useNavigate } from 'react-router-dom';
import { signupUser, clearError } from '../redux/slices/authSlice';
import Button from '../components/Button';
import { uploadFile } from '../services/fileUploadService';
import { Image, Upload } from 'lucide-react';

const Signup = () => {
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    password: '',
  });
  const [avatarFile, setAvatarFile] = useState(null);
  const [previewUrl, setPreviewUrl] = useState(null);
  const [isUploading, setIsUploading] = useState(false);
  
  const fileInputRef = useRef(null);
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { isLoading, error } = useSelector((state) => state.auth);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    if (error) dispatch(clearError());
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setAvatarFile(file);
      setPreviewUrl(URL.createObjectURL(file));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsUploading(true);
    
    try {
      let avatarUrl = '';
      if (avatarFile) {
        avatarUrl = await uploadFile(avatarFile);
      }

      const signupData = {
        username: formData.fullName, // Mapping fullName to username as per backend DTO
        email: formData.email,
        password: formData.password,
        avatar: avatarUrl
      };

      const result = await dispatch(signupUser(signupData));
      if (signupUser.fulfilled.match(result)) {
        navigate('/login');
      }
    } catch (err) {
      console.error("Signup failed", err);
    } finally {
      setIsUploading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 p-4">
      <div className="w-full max-w-[350px] space-y-4">
        {/* Main Card */}
        <div className="bg-white border border-gray-300 p-8 md:p-10 flex flex-col items-center text-center">
          <h1 className="text-4xl font-serif font-bold mb-4 tracking-wide">ChatterBox</h1>
          
          <p className="text-gray-500 font-semibold text-base mb-6 leading-5">
            Sign up to see photos and videos from your friends.
          </p>

          <Button className="w-full py-1.5 text-sm font-semibold rounded-[4px] bg-blue-500 hover:bg-blue-600 text-white shadow-none mb-4">
            Log in with Facebook
          </Button>

          <div className="flex items-center w-full mb-4">
            <div className="h-px bg-gray-300 flex-1"></div>
            <span className="px-4 text-xs text-gray-500 font-semibold uppercase">OR</span>
            <div className="h-px bg-gray-300 flex-1"></div>
          </div>

          {error && (
            <div className="w-full mb-4">
              <p className="text-sm text-red-500">{error}</p>
            </div>
          )}

          <form onSubmit={handleSubmit} className="w-full space-y-2">
            {/* Avatar Upload */}
            <div className="flex justify-center mb-4">
              <div 
                className="w-20 h-20 rounded-full bg-gray-100 border border-gray-200 overflow-hidden cursor-pointer relative group"
                onClick={() => fileInputRef.current?.click()}
              >
                {previewUrl ? (
                  <img src={previewUrl} alt="Avatar Preview" className="w-full h-full object-cover" />
                ) : (
                  <div className="w-full h-full flex items-center justify-center text-gray-400">
                    <Upload className="w-8 h-8" />
                  </div>
                )}
                <div className="absolute inset-0 bg-black/20 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center text-white text-xs font-medium">
                  Upload
                </div>
              </div>
              <input 
                type="file" 
                ref={fileInputRef} 
                onChange={handleFileChange} 
                accept="image/*" 
                className="hidden" 
              />
            </div>

            <div>
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                placeholder="Mobile Number or Email"
                required
                className="w-full px-2 py-2.5 bg-gray-50 border border-gray-300 rounded-sm text-xs focus:outline-none focus:border-gray-400"
              />
            </div>
            <div>
              <input
                type="text"
                name="fullName"
                value={formData.fullName}
                onChange={handleChange}
                placeholder="Full Name"
                required
                className="w-full px-2 py-2.5 bg-gray-50 border border-gray-300 rounded-sm text-xs focus:outline-none focus:border-gray-400"
              />
            </div>
            <div>
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
            
            <p className="text-xs text-gray-500 my-4">
              By signing up, you agree to our Terms, Privacy Policy and Cookies Policy.
            </p>

            <Button 
              type="submit" 
              className="w-full py-1.5 text-sm font-semibold rounded-[4px] bg-blue-500 hover:bg-blue-600 text-white shadow-none" 
              isLoading={isLoading || isUploading}
            >
              Sign up
            </Button>
          </form>
        </div>

        {/* Login link */}
        <div className="bg-white border border-gray-300 p-5 text-center">
          <p className="text-sm">
            Have an account?{' '}
            <Link to="/login" className="font-semibold text-blue-500">
              Log in
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Signup;
