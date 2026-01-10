import axios from 'axios';

const CLOUDINARY_URL = 'https://api.cloudinary.com/v1_1/dzedg7ky2/auto/upload';
const UPLOAD_PRESET = 'jjuj8rvs';

export const uploadFile = async (file) => {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('upload_preset', UPLOAD_PRESET);

  try {
    const response = await axios.post(CLOUDINARY_URL, formData);
    return response.data.secure_url;
  } catch (error) {
    console.error('Error uploading file:', error);
    throw error;
  }
};
