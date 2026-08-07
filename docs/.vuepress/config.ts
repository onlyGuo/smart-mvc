import { viteBundler } from '@vuepress/bundler-vite'
import { searchPlugin } from '@vuepress/plugin-search'
import { defaultTheme } from '@vuepress/theme-default'
import { defineUserConfig } from 'vuepress'

const siteBase = process.env.VUEPRESS_BASE || '/'

const route = (prefix: string, path: string) =>
  prefix === '/' ? `/${path}` : `${prefix}${path}`

const createSidebar = (
  prefix: string,
  labels: {
    guide: string
    features: string
    auth: string
    examples: string
    reference: string
  },
) => ({
  [route(prefix, 'guide/')]: [
    {
      text: labels.guide,
      collapsible: false,
      children: [
        route(prefix, 'guide/introduction.md'),
        route(prefix, 'guide/getting-started.md'),
        route(prefix, 'guide/how-it-works.md'),
      ],
    },
  ],
  [route(prefix, 'features/')]: [
    {
      text: labels.features,
      collapsible: false,
      children: [
        route(prefix, 'features/response.md'),
        route(prefix, 'features/exceptions.md'),
        route(prefix, 'features/date-time.md'),
        route(prefix, 'features/request-logging.md'),
      ],
    },
  ],
  [route(prefix, 'auth/')]: [
    {
      text: labels.auth,
      collapsible: false,
      children: [
        route(prefix, 'auth/overview.md'),
        route(prefix, 'auth/custom-authentication.md'),
      ],
    },
  ],
  [route(prefix, 'examples/')]: [
    {
      text: labels.examples,
      collapsible: false,
      children: [route(prefix, 'examples/login.md')],
    },
  ],
  [route(prefix, 'reference/')]: [
    {
      text: labels.reference,
      collapsible: false,
      children: [
        route(prefix, 'reference/configuration.md'),
        route(prefix, 'reference/api.md'),
      ],
    },
  ],
})

export default defineUserConfig({
  base: siteBase,
  bundler: viteBundler(),
  head: [
    ['meta', { name: 'theme-color', content: '#101918' }],
    ['meta', { name: 'color-scheme', content: 'light dark' }],
  ],
  locales: {
    '/': {
      lang: 'zh-CN',
      title: 'SmartMVC',
      description: '统一 Spring MVC 响应、异常、时间、日志与认证授权。',
      head: [
        ['meta', { property: 'og:type', content: 'website' }],
        ['meta', { property: 'og:title', content: 'SmartMVC 中文文档' }],
        ['meta', { property: 'og:description', content: '从快速开始到完整参考，逐步掌握 SmartMVC。' }],
      ],
    },
    '/en/': {
      lang: 'en-US',
      title: 'SmartMVC',
      description: 'Consistent responses, errors, time, logging, and authentication for Spring MVC.',
      head: [
        ['meta', { property: 'og:type', content: 'website' }],
        ['meta', { property: 'og:title', content: 'SmartMVC Documentation' }],
        ['meta', { property: 'og:description', content: 'Learn SmartMVC progressively, from quick start to full reference.' }],
      ],
    },
    '/ja/': {
      lang: 'ja-JP',
      title: 'SmartMVC',
      description: 'Spring MVC のレスポンス、例外、日時、ログ、認証・認可を統一します。',
      head: [
        ['meta', { property: 'og:type', content: 'website' }],
        ['meta', { property: 'og:title', content: 'SmartMVC 日本語ドキュメント' }],
        ['meta', { property: 'og:description', content: 'クイックスタートからリファレンスまで、SmartMVC を段階的に学べます。' }],
      ],
    },
    '/ru/': {
      lang: 'ru-RU',
      title: 'SmartMVC',
      description: 'Единые ответы, ошибки, дата и время, журналы и аутентификация для Spring MVC.',
      head: [
        ['meta', { property: 'og:type', content: 'website' }],
        ['meta', { property: 'og:title', content: 'Документация SmartMVC на русском' }],
        ['meta', { property: 'og:description', content: 'Последовательное изучение SmartMVC: от быстрого старта до справочника.' }],
      ],
    },
  },
  theme: defaultTheme({
    repo: 'onlyGuo/smart-mvc',
    docsDir: 'docs',
    logoAlt: 'SmartMVC home',
    colorMode: 'auto',
    colorModeSwitch: true,
    contributors: false,
    lastUpdated: true,
    locales: {
      '/': {
        selectLanguageName: '简体中文',
        selectLanguageText: '语言',
        selectLanguageAriaLabel: '选择语言',
        editLinkText: '在 GitHub 上编辑此页',
        lastUpdatedText: '最后更新',
        prev: '上一页',
        next: '下一页',
        tip: '提示',
        warning: '注意',
        danger: '危险',
        notFound: ['页面不存在或已经移动。', '请从导航继续浏览文档。'],
        backToHome: '返回首页',
        toggleColorMode: '切换颜色模式',
        toggleSidebar: '切换侧边栏',
        navbar: [
          { text: '全部文档', link: '/contents.html' },
          { text: '指南', link: '/guide/introduction.html' },
          { text: '功能', link: '/features/response.html' },
          { text: '认证授权', link: '/auth/overview.html' },
          { text: '示例', link: '/examples/login.html' },
          { text: '参考', link: '/reference/configuration.html' },
        ],
        sidebar: createSidebar('/', {
          guide: '开始使用',
          features: '核心功能',
          auth: '认证授权',
          examples: '完整示例',
          reference: '参考手册',
        }),
      },
      '/en/': {
        selectLanguageName: 'English',
        selectLanguageText: 'Languages',
        selectLanguageAriaLabel: 'Select language',
        editLinkText: 'Edit this page on GitHub',
        lastUpdatedText: 'Last updated',
        prev: 'Previous',
        next: 'Next',
        tip: 'Tip',
        warning: 'Warning',
        danger: 'Danger',
        notFound: ['This page does not exist or has moved.', 'Continue from the documentation navigation.'],
        backToHome: 'Back to home',
        toggleColorMode: 'Toggle color mode',
        toggleSidebar: 'Toggle sidebar',
        navbar: [
          { text: 'All docs', link: '/en/contents.html' },
          { text: 'Guide', link: '/en/guide/introduction.html' },
          { text: 'Features', link: '/en/features/response.html' },
          { text: 'Authentication', link: '/en/auth/overview.html' },
          { text: 'Example', link: '/en/examples/login.html' },
          { text: 'Reference', link: '/en/reference/configuration.html' },
        ],
        sidebar: createSidebar('/en/', {
          guide: 'Getting started',
          features: 'Core features',
          auth: 'Authentication',
          examples: 'Complete example',
          reference: 'Reference',
        }),
      },
      '/ja/': {
        selectLanguageName: '日本語',
        selectLanguageText: '言語',
        selectLanguageAriaLabel: '言語を選択',
        editLinkText: 'GitHub でこのページを編集',
        lastUpdatedText: '最終更新',
        prev: '前へ',
        next: '次へ',
        tip: 'ヒント',
        warning: '注意',
        danger: '危険',
        notFound: ['このページは存在しないか、移動しました。', 'ナビゲーションからドキュメントをお読みください。'],
        backToHome: 'ホームへ戻る',
        toggleColorMode: 'カラーモードを切り替える',
        toggleSidebar: 'サイドバーを切り替える',
        navbar: [
          { text: 'ドキュメント一覧', link: '/ja/contents.html' },
          { text: 'ガイド', link: '/ja/guide/introduction.html' },
          { text: '機能', link: '/ja/features/response.html' },
          { text: '認証・認可', link: '/ja/auth/overview.html' },
          { text: 'サンプル', link: '/ja/examples/login.html' },
          { text: 'リファレンス', link: '/ja/reference/configuration.html' },
        ],
        sidebar: createSidebar('/ja/', {
          guide: 'はじめに',
          features: '主な機能',
          auth: '認証・認可',
          examples: '完全な例',
          reference: 'リファレンス',
        }),
      },
      '/ru/': {
        selectLanguageName: 'Русский',
        selectLanguageText: 'Язык',
        selectLanguageAriaLabel: 'Выбрать язык',
        editLinkText: 'Изменить эту страницу на GitHub',
        lastUpdatedText: 'Последнее обновление',
        prev: 'Назад',
        next: 'Далее',
        tip: 'Подсказка',
        warning: 'Внимание',
        danger: 'Опасно',
        notFound: ['Страница не существует или была перемещена.', 'Продолжите чтение с помощью навигации.'],
        backToHome: 'На главную',
        toggleColorMode: 'Переключить цветовую тему',
        toggleSidebar: 'Переключить боковую панель',
        navbar: [
          { text: 'Все документы', link: '/ru/contents.html' },
          { text: 'Руководство', link: '/ru/guide/introduction.html' },
          { text: 'Возможности', link: '/ru/features/response.html' },
          { text: 'Аутентификация', link: '/ru/auth/overview.html' },
          { text: 'Пример', link: '/ru/examples/login.html' },
          { text: 'Справочник', link: '/ru/reference/configuration.html' },
        ],
        sidebar: createSidebar('/ru/', {
          guide: 'Начало работы',
          features: 'Основные возможности',
          auth: 'Аутентификация',
          examples: 'Полный пример',
          reference: 'Справочник',
        }),
      },
    },
  }),
  plugins: [
    searchPlugin({
      locales: {
        '/': { placeholder: '搜索文档' },
        '/en/': { placeholder: 'Search documentation' },
        '/ja/': { placeholder: 'ドキュメントを検索' },
        '/ru/': { placeholder: 'Поиск по документации' },
      },
      maxSuggestions: 10,
    }),
  ],
})
