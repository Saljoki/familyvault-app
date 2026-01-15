import { useState } from 'react';
import { filesApi } from '../api/files';

export default function FileUpload({ familyId, folderId, onSuccess }) {
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState('');
  const [progress, setProgress] = useState(0);

  const handleFileSelect = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    setUploading(true);
    setError('');
    setProgress(0);

    try {
      // Step 1: Initiate upload
      const initResponse = await filesApi.initiateUpload({
        familyId,
        folderId: folderId || null,
        fileName: file.name,
        contentType: file.type || 'application/octet-stream',
        fileSize: file.size,
      });

      setProgress(33);

      // Step 2: Upload to presigned URL
      await fetch(initResponse.uploadUrl, {
        method: 'PUT',
        body: file,
        headers: {
          'Content-Type': file.type || 'application/octet-stream',
        },
      });

      setProgress(66);

      // Step 3: Confirm upload
      await filesApi.confirmUpload(initResponse.fileId);

      setProgress(100);
      onSuccess();
      e.target.value = ''; // Reset input
    } catch (err) {
      setError(err.response?.data?.message || 'Upload failed');
    } finally {
      setUploading(false);
    }
  };

  return (
    <div>
      <input
        type="file"
        onChange={handleFileSelect}
        disabled={uploading}
        className="hidden"
        id="file-upload"
      />
      <label
        htmlFor="file-upload"
        className={`inline-block px-4 py-2 bg-blue-600 text-white rounded-md cursor-pointer hover:bg-blue-700 transition-colors ${
          uploading ? 'opacity-50 cursor-not-allowed' : ''
        }`}
      >
        {uploading ? `Uploading... ${progress}%` : 'Upload File'}
      </label>
      {error && <p className="text-red-600 text-sm mt-2">{error}</p>}
    </div>
  );
}
