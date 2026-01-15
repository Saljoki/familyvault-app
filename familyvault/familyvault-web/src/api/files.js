import apiClient from './client';

export const filesApi = {
  initiateUpload: async (data) => {
    const response = await apiClient.post('/files/upload/initiate', data);
    return response.data;
  },

  confirmUpload: async (fileId) => {
    const response = await apiClient.post(`/files/upload/${fileId}/confirm`);
    return response.data;
  },

  getFiles: async (params) => {
    const response = await apiClient.get('/files', { params });
    return response.data;
  },

  getDownloadUrl: async (fileId) => {
    const response = await apiClient.get(`/files/${fileId}/download-url`);
    return response.data;
  },

  getViewUrl: async (fileId) => {
    const response = await apiClient.get(`/files/${fileId}/view-url`);
    return response.data;
  },

  deleteFile: async (fileId) => {
    const response = await apiClient.delete(`/files/${fileId}`);
    return response.data;
  },
};
