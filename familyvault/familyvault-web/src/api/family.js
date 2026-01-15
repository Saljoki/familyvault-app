import apiClient from './client';

export const familyApi = {
  createFamily: async (data) => {
    const response = await apiClient.post('/families', data);
    return response.data;
  },

  joinFamily: async (inviteCode) => {
    const response = await apiClient.post('/families/join', { inviteCode });
    return response.data;
  },

  getFamilies: async () => {
    const response = await apiClient.get('/families');
    return response.data;
  },

  getFamily: async (familyId) => {
    const response = await apiClient.get(`/families/${familyId}`);
    return response.data;
  },

  getMembers: async (familyId) => {
    const response = await apiClient.get(`/families/${familyId}/members`);
    return response.data;
  },

  regenerateInviteCode: async (familyId) => {
    const response = await apiClient.post(`/families/${familyId}/invite/regenerate`);
    return response.data;
  },

  toggleInvite: async (familyId, enabled) => {
    const response = await apiClient.put(`/families/${familyId}/invite/toggle`, null, {
      params: { enabled },
    });
    return response.data;
  },

  updateMemberRole: async (familyId, userId, role) => {
    const response = await apiClient.put(`/families/${familyId}/members/${userId}/role`, { role });
    return response.data;
  },

  removeMember: async (familyId, userId) => {
    const response = await apiClient.delete(`/families/${familyId}/members/${userId}`);
    return response.data;
  },
};
