import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Home, Search, Heart, User, PlusSquare, LogOut, MessageCircle } from 'lucide-react';
import { useDispatch } from 'react-redux';
import { logout } from '../redux/slices/authSlice';

const Sidebar = () => {
  const location = useLocation();
  const dispatch = useDispatch();

  const navItems = [
    { icon: Home, label: 'Home', path: '/' },
    { icon: Search, label: 'Search', path: '/search' },

    { icon: MessageCircle, label: 'Messages', path: '/chat' },
    { icon: Heart, label: 'Notifications', path: '/notifications' },
    { icon: User, label: 'Profile', path: '/profile' },
  ];

  const handleLogout = () => {
    dispatch(logout());
  };

  return (
    <div className="hidden md:flex flex-col w-64 bg-white border-r border-gray-200 h-screen fixed left-0 top-0 z-50 px-4 py-8">

      <div className="mb-10 px-4">
        <h1 className="text-2xl font-bold font-serif tracking-wider">ChatterBox</h1>
      </div>


      <nav className="flex-1 space-y-2">
        {navItems.map((item) => {
          const Icon = item.icon;
          const isActive = location.pathname === item.path;

          return (
            <Link
              key={item.path}
              to={item.path}
              className={`flex items-center space-x-4 px-4 py-3 rounded-lg transition-all duration-200 group ${isActive
                ? 'font-bold text-black'
                : 'text-gray-700 hover:bg-gray-50'
                }`}
            >
              <Icon
                className={`w-7 h-7 transition-transform duration-200 ${isActive ? 'scale-110 stroke-[2.5px]' : 'group-hover:scale-105'}`}
              />
              <span className="text-base">{item.label}</span>
            </Link>
          );
        })}
      </nav>


      <div className="mt-auto pt-4 border-t border-gray-100">
        <button
          onClick={handleLogout}
          className="flex items-center space-x-4 px-4 py-3 w-full text-left text-gray-700 hover:bg-gray-50 rounded-lg transition-colors duration-200"
        >
          <LogOut className="w-7 h-7" />
          <span className="text-base">Log out</span>
        </button>
      </div>
    </div>
  );
};

export default Sidebar;
