# Secured Health Records Backend - Render Deployment Guide

This guide will help you deploy the Secured Health Records backend to Render.

## Prerequisites

1. **Render Account**: Sign up at [render.com](https://render.com)
2. **MongoDB Atlas**: Set up a MongoDB cluster at [mongodb.com/atlas](https://mongodb.com/atlas)
3. **Cloudinary Account**: Sign up at [cloudinary.com](https://cloudinary.com)
4. **MailerSend Account**: Sign up at [mailersend.com](https://mailersend.com)

## Environment Variables Setup

You'll need to configure these environment variables in Render:

### Database
- `MONGODB_URI`: Your MongoDB Atlas connection string
  - Example: `mongodb+srv://username:password@cluster.mongodb.net/secured_health_records`

### JWT Configuration
- `JWT_SECRET`: A secure 256-bit secret key for JWT tokens
  - Generate with: `openssl rand -base64 32`
- `JWT_EXPIRATION`: Token expiration time in milliseconds (default: 86400000 = 24 hours)

### Cloudinary (File Storage)
- `CLOUDINARY_CLOUD_NAME`: Your Cloudinary cloud name
- `CLOUDINARY_API_KEY`: Your Cloudinary API key
- `CLOUDINARY_API_SECRET`: Your Cloudinary API secret

### MailerSend (Email Service)
- `MAILERSEND_API_KEY`: Your MailerSend API key
- `MAILERSEND_FROM_EMAIL`: Verified sender email address
- `MAILERSEND_FROM_NAME`: Sender display name (default: "Secured Health Records")

### Frontend Configuration
- `FRONTEND_URL`: Your frontend application URL
  - Example: `https://your-frontend-app.onrender.com`
- `CORS_ALLOWED_ORIGINS`: Comma-separated list of allowed origins
  - Example: `https://your-frontend-app.onrender.com,http://localhost:3000`

## Deployment Steps

### Option 1: Using render.yaml (Recommended)

1. **Connect Repository**: Connect your GitHub repository to Render
2. **Auto-Deploy**: Render will automatically detect the `render.yaml` file
3. **Set Environment Variables**: Configure all required environment variables in Render dashboard
4. **Deploy**: Render will build and deploy your application automatically

### Option 2: Manual Setup

1. **Create Web Service**: 
   - Environment: Java
   - Build Command: `mvn clean package -DskipTests`
   - Start Command: `java -jar target/secured-health-records-backend-0.0.1-SNAPSHOT.jar`

2. **Configure Environment Variables**: Add all the environment variables listed above

3. **Deploy**: Render will build and deploy your application

## API Endpoints

Once deployed, your API will be available at:
- Base URL: `https://your-app-name.onrender.com/api/v1`

### Authentication Endpoints
- `POST /auth/register` - User registration
- `POST /auth/login` - User login
- `POST /auth/send-otp` - Send OTP for verification
- `POST /auth/verify-otp` - Verify OTP

### File Management Endpoints
- `GET /nodes` - Get user's files and folders
- `POST /nodes/folder` - Create folder
- `POST /nodes/file` - Upload file (multipart/form-data)
- `PUT /nodes/{id}` - Update file/folder name
- `DELETE /nodes/{id}` - Delete file/folder

### Sharing Endpoints
- `POST /share` - Create share link
- `GET /share/view/{token}` - View shared files
- `GET /share/patient/shares` - Get user's shares
- `DELETE /share/{id}` - Revoke share

### Hospital Directory
- `GET /hospitals` - List all hospitals
- `GET /hospitals/search` - Search hospitals

## Security Features

- **JWT Authentication**: Stateless authentication with secure tokens
- **CORS Protection**: Configurable cross-origin request handling
- **Input Validation**: Request validation with proper error handling
- **File Encryption**: Client-side encryption with secure key management
- **Secure Sharing**: Time-limited share links with access tokens

## Monitoring and Logs

- **Health Check**: The application includes built-in health monitoring
- **Logging**: Comprehensive logging for debugging and monitoring
- **Error Handling**: Global exception handling with proper HTTP status codes

## Troubleshooting

### Common Issues

1. **Database Connection**: Ensure MongoDB URI is correct and cluster is accessible
2. **CORS Errors**: Verify CORS_ALLOWED_ORIGINS includes your frontend URL
3. **File Upload Issues**: Check Cloudinary credentials and configuration
4. **Email Not Sending**: Verify MailerSend API key and sender email verification

### Support

For deployment issues, check:
1. Render build logs
2. Application logs in Render dashboard
3. Environment variable configuration
4. Service dependencies (MongoDB, Cloudinary, MailerSend)

## Production Considerations

1. **Database Scaling**: Consider MongoDB Atlas scaling options
2. **File Storage**: Monitor Cloudinary usage and limits
3. **Email Limits**: Check MailerSend sending limits
4. **Security**: Regularly rotate JWT secrets and API keys
5. **Backup**: Set up regular database backups
6. **Monitoring**: Implement application performance monitoring
