import { defineClientConfig } from 'vuepress/client'
import HomeShowcase from './components/HomeShowcase.vue'

export default defineClientConfig({
  enhance({ app }) {
    app.component('HomeShowcase', HomeShowcase)
  },
})
