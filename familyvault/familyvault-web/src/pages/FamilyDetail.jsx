import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { familyApi } from '../api/family';
import { filesApi } from '../api/files';

export default function FamilyDetail() {
  const { familyId } = useParams();
  const navigate = useNavigate();
  const [family, setFamily] = useState(null);
  const [members, setMembers] = useState([]);
  const [files, setFiles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('files');

  useEffect(() => {
    loadFamilyData();
  }, [familyId]);

  const loadFamilyData = async () => {
    try {
      const [familyData, membersData, filesData] = await Promise.all([
        familyApi.getFamily(familyId),
        familyApi.getMembers(familyId),
        filesApi.getFiles({ familyId, page: 0, size: 20 }),
      ]);
      setFamily(familyData);
      setMembers(membersData);
      setFiles(filesData.content || filesData);
    } catch (error) {
      console.error('Failed to load family data:', error);
    } finally {
      setLoading(false);
    }
  };

  const formatBytes = (bytes) => {
    if (bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
  };

  if (loading) {
    return <div className="flex justify-center items-center min-h-screen">Loading...</div>;
  }

  if (!family) {
    return <div className="flex justify-center items-center min-h-screen">Family not found</div>;
  }

  return (
    <div className="min-h-screen bg-gray-100">
      <nav className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center space-x-4">
              <button
                onClick={() => navigate('/families')}
                className="text-gray-600 hover:text-gray-900"
              >
                ← Back
              </button>
              <h1 className="text-2xl font-bold text-gray-900">{family.name}</h1>
            </div>
          </div>
        </div>
      </nav>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div>
              <p className="text-gray-600 text-sm">Storage Used</p>
              <p className="text-2xl font-bold">{formatBytes(family.storageUsedBytes)}</p>
            </div>
            <div>
              <p className="text-gray-600 text-sm">Storage Limit</p>
              <p className="text-2xl font-bold">{formatBytes(family.storageLimitBytes)}</p>
            </div>
            <div>
              <p className="text-gray-600 text-sm">Usage</p>
              <p className="text-2xl font-bold">{family.storageUsagePercentage.toFixed(1)}%</p>
            </div>
          </div>

          {family.inviteEnabled && (
            <div className="mt-6 pt-6 border-t">
              <p className="text-gray-600 text-sm mb-2">Invite Code</p>
              <div className="flex items-center space-x-3">
                <code className="bg-gray-100 px-4 py-2 rounded font-mono text-lg">
                  {family.inviteCode}
                </code>
                <button
                  onClick={() => navigator.clipboard.writeText(family.inviteCode)}
                  className="text-blue-600 hover:text-blue-700 text-sm font-medium"
                >
                  Copy
                </button>
              </div>
            </div>
          )}
        </div>

        <div className="bg-white rounded-lg shadow-md">
          <div className="border-b">
            <nav className="flex">
              <button
                onClick={() => setActiveTab('files')}
                className={`px-6 py-4 font-medium ${
                  activeTab === 'files'
                    ? 'border-b-2 border-blue-600 text-blue-600'
                    : 'text-gray-600 hover:text-gray-900'
                }`}
              >
                Files
              </button>
              <button
                onClick={() => setActiveTab('members')}
                className={`px-6 py-4 font-medium ${
                  activeTab === 'members'
                    ? 'border-b-2 border-blue-600 text-blue-600'
                    : 'text-gray-600 hover:text-gray-900'
                }`}
              >
                Members
              </button>
            </nav>
          </div>

          <div className="p-6">
            {activeTab === 'files' && (
              <div>
                {files.length === 0 ? (
                  <p className="text-gray-500 text-center py-8">No files uploaded yet</p>
                ) : (
                  <div className="space-y-2">
                    {files.map((file) => (
                      <div
                        key={file.id}
                        className="flex items-center justify-between p-3 hover:bg-gray-50 rounded"
                      >
                        <div>
                          <p className="font-medium">{file.originalName}</p>
                          <p className="text-sm text-gray-500">
                            {formatBytes(file.fileSize)} • {new Date(file.uploadedAt).toLocaleDateString()}
                          </p>
                        </div>
                        <button
                          onClick={async () => {
                            const { url } = await filesApi.getDownloadUrl(file.id);
                            window.open(url, '_blank');
                          }}
                          className="text-blue-600 hover:text-blue-700 text-sm font-medium"
                        >
                          Download
                        </button>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}

            {activeTab === 'members' && (
              <div className="space-y-2">
                {members.map((member) => (
                  <div
                    key={member.id}
                    className="flex items-center justify-between p-3 hover:bg-gray-50 rounded"
                  >
                    <div>
                      <p className="font-medium">
                        {member.userFirstName} {member.userLastName}
                      </p>
                      <p className="text-sm text-gray-500">{member.userEmail}</p>
                    </div>
                    <span className="px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm">
                      {member.role}
                    </span>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
