# FamilyVault Web

React frontend for FamilyVault - A family cloud storage platform.

## Features

- User authentication (Register, Login, Logout)
- Family management (Create, Join via invite code)
- File browsing and download
- Member management
- Storage usage tracking

## Getting Started

### Prerequisites

- Node.js 18+ and npm

### Installation

1. Install dependencies:
```bash
npm install
```

2. Create `.env` file from example:
```bash
cp .env.example .env
```

3. Update `.env` with your backend API URL (default: http://localhost:8080/api/v1)

### Development

Run the development server:
```bash
npm run dev
```

The app will be available at http://localhost:5173

### Build

Build for production:
```bash
npm run build
```

The built files will be in the `dist/` directory.

## Tech Stack

- React 18
- Vite
- React Router 6
- Axios
- React Query (for future enhancements)

## Project Structure

```
src/
├── api/           # API client and service functions
├── context/       # React contexts (Auth, etc.)
├── pages/         # Page components
│   ├── Login.jsx
│   ├── Register.jsx
│   ├── Families.jsx
│   └── FamilyDetail.jsx
├── App.jsx        # Main app with routing
└── main.jsx       # Entry point
```
