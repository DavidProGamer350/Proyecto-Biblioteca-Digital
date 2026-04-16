import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/auth': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/users': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/books': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/prestamos': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/recomendaciones': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})