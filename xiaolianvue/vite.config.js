import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import Icons from 'unplugin-icons/vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    Icons({ compiler: 'vue3' })
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    host: "127.0.0.1",
    port: 5173,
  },

})
