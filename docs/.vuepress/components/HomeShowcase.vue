<script setup lang="ts">
import { computed, ref } from 'vue'
import { withBase } from 'vuepress/client'

type Locale = 'zh' | 'en' | 'ja' | 'ru'

const props = withDefaults(defineProps<{ lang?: Locale }>(), { lang: 'zh' })
const activeCode = ref<'controller' | 'auth' | 'config'>('controller')
const copied = ref(false)

const messages = {
  zh: {
    heroLine1: '该做的，都帮你做好。', heroLine2: '不该做的，一律不碰。',
    heroDescription: 'SmartMVC 把统一响应、异常处理、日期时间、请求日志和认证授权这些重复的 MVC 基础工作收拢起来。它不接管业务逻辑，不强迫你继承基类，也不替你决定 Spring Web 与 Bean Validation 的版本。你仍然用熟悉的方式写 Spring MVC。',
    primary: '从这里开始', secondary: '浏览完整目录', ideMetadata: 'IDE 配置补全', springFree: 'Core 不依赖 Spring', boundary: '替你处理重复，不替你做决定',
    traceLabel: 'SmartMVC 受保护请求处理示例', traceHandler: '找到 Controller', traceIdentity: '按配置检查认证', traceResponse: '整理统一响应',
    principlesOverline: '有能力，也有边界', principlesTitle: '重复的事交给框架。\n重要的决定留给你。',
    principles: [
      ['统一接口约定', '成功响应、错误响应和字段校验详情使用稳定结构，客户端可以统一处理。'],
      ['集中管理 MVC 配置', '日期格式、时区、日志级别和认证范围都位于 spring.smart.mvc 命名空间。'],
      ['保留应用控制权', '核心模型不依赖 Spring，关键 Bean 和认证过程都可以由应用替换。'],
    ],
    capabilitiesOverline: '主要能力', capabilitiesTitle: '从你最关心的地方\n开始。', capabilitiesDescription: '每项能力都有独立的入口和清晰的边界。你可以按顺序学，也可以直接进入今天正好需要的那一项。', featureLabels: ['统一响应', '异常处理', '日期时间', '认证授权', '请求日志'], featureLink: '查看功能', catalogLink: '查看全部文档',
    features: [
      ['统一响应与包装', '普通 Controller 返回值自动包装为 ApiResponse；ApiResponse、byte[]、Resource、StreamingResponseBody 和 ProblemDetail 保持原样。'],
      ['异常与校验', '常用 HTTP 异常、业务错误和 Bean Validation 使用同一错误结构。'],
      ['日期与时间', '统一处理 LocalDate、Instant、OffsetDateTime、ZonedDateTime 和 Date。'],
      ['认证授权', '支持 GLOBAL、ANNOTATED、角色、命名权限和 METHOD:PATH 请求权限。'],
      ['请求日志', '使用实际 Controller 作为日志类别，记录状态码和处理耗时。'],
    ],
    authOverline: '当前认证', authTitle: '在任何 Spring Bean 中\n读取当前用户。', authDescription: 'CurrentAuth 是可注入的单例门面，身份数据按同步 Servlet 请求隔离，并在请求完成后清理。', authLink: '了解认证授权', identityLabel: '请求身份隔离示意图', singletonLabel: 'Spring 单例', facadeOnly: '只提供访问门面', anonymous: '匿名请求', noIdentity: '不创建身份', isolated: '请求隔离',
    codeOverline: '保持熟悉的写法', codeTitle: 'Controller 仍然只是 Controller。', codeDescription: '无需继承框架基类，也无需为每个返回值手工创建响应包装。', benefits: ['只需额外添加一个 SmartMVC 依赖', 'IDE 可补全全部配置', '关键扩展点可以替换'], copy: '复制', copied: '已复制', codeLabel: '代码示例',
    closingOverline: '从基础开始', closingTitle: '先运行第一个接口，再按需要逐项深入。', closingAction: '阅读快速开始', footer: '让 Spring MVC 项目更统一、更容易维护。',
  },
  en: {
    heroLine1: 'The MVC groundwork, handled.', heroLine2: 'Your application, still yours.',
    heroDescription: 'SmartMVC brings consistent responses, error handling, date and time, request logging, authentication, and authorization into one focused layer. It never takes over your business logic, forces a base class, or dictates your Spring Web and Bean Validation versions. You keep writing Spring MVC the way you know.',
    primary: 'Start here', secondary: 'Browse all docs', ideMetadata: 'IDE configuration metadata', springFree: 'Spring-free core', boundary: 'Handles repetition without taking decisions away',
    traceLabel: 'Example of a protected request in SmartMVC', traceHandler: 'Controller matched', traceIdentity: 'Authentication rules evaluated', traceResponse: 'Response standardized',
    principlesOverline: 'CAPABILITY WITH BOUNDARIES', principlesTitle: 'Give repetition to the framework.\nKeep important choices with you.',
    principles: [
      ['A stable API contract', 'Success, failure, and field validation details follow structures clients can handle consistently.'],
      ['Central MVC configuration', 'Date formats, zones, log levels, and authentication scope live under spring.smart.mvc.'],
      ['Application control remains', 'Core stays independent of Spring, and important beans and authentication behavior remain replaceable.'],
    ],
    capabilitiesOverline: 'CORE CAPABILITIES', capabilitiesTitle: 'Start with what\nyou need today.', capabilitiesDescription: 'Every capability has its own entry point and a clear boundary. Follow the learning path, or go straight to the part your application needs right now.', featureLabels: ['RESPONSE', 'EXCEPTION', 'DATE & TIME', 'AUTH', 'REQUEST LOG'], featureLink: 'Explore this feature', catalogLink: 'Browse all documentation',
    features: [
      ['Responses and wrapping', 'Ordinary controller results become ApiResponse; ApiResponse, byte[], Resource, StreamingResponseBody, and ProblemDetail pass through unchanged.'],
      ['Errors and validation', 'HTTP failures, business errors, and Bean Validation share one predictable structure.'],
      ['Date and time', 'Consistent handling for LocalDate, Instant, OffsetDateTime, ZonedDateTime, and Date.'],
      ['Authentication and authorization', 'GLOBAL and ANNOTATED scopes with roles, named permissions, and METHOD:PATH rules.'],
      ['Request logging', 'The actual controller becomes the logger category, with status and elapsed time recorded.'],
    ],
    authOverline: 'CURRENT AUTH', authTitle: 'Read the current user\nfrom any Spring bean.', authDescription: 'CurrentAuth is an injectable singleton facade. Identity data is isolated per synchronous Servlet request and cleared when processing finishes.', authLink: 'Learn authentication', identityLabel: 'Request identity isolation diagram', singletonLabel: 'SPRING SINGLETON', facadeOnly: 'access facade only', anonymous: 'Anonymous', noIdentity: 'no identity created', isolated: 'isolated',
    codeOverline: 'KEEP THE FAMILIAR MODEL', codeTitle: 'A controller remains a controller.', codeDescription: 'No framework base class and no manual response envelope around every return value.', benefits: ['Only one additional SmartMVC dependency', 'IDE completion for every property', 'Important extension points remain replaceable'], copy: 'Copy', copied: 'Copied', codeLabel: 'Code examples',
    closingOverline: 'START WITH THE BASICS', closingTitle: 'Run one endpoint, then explore each capability as you need it.', closingAction: 'Read the quick start', footer: 'Make Spring MVC projects more consistent and easier to maintain.',
  },
  ja: {
    heroLine1: 'MVC の基盤は、きちんと。', heroLine2: '大切な判断は、あなたに。',
    heroDescription: 'SmartMVC は、レスポンス、例外、日時、リクエストログ、認証・認可といった繰り返しの多い MVC 基盤を一つに整えます。業務ロジックには介入せず、基底クラスを強制せず、Spring Web や Bean Validation のバージョンも決めません。いつもの Spring MVC の書き方を、そのまま使えます。',
    primary: 'ここから始める', secondary: 'ドキュメント一覧', ideMetadata: 'IDE の設定補完', springFree: 'Core は Spring 非依存', boundary: '繰り返しは整え、アプリの判断は奪わない',
    traceLabel: 'SmartMVC で保護されたリクエストの処理例', traceHandler: 'Controller を特定', traceIdentity: '設定に従って認証ルールを確認', traceResponse: 'レスポンスを統一',
    principlesOverline: '機能と境界', principlesTitle: '繰り返しはフレームワークへ。\n大切な判断はアプリ側に。',
    principles: [
      ['安定した API 契約', '成功、失敗、フィールド検証の詳細を、クライアントが一貫して扱える構造にします。'],
      ['MVC 設定を一か所へ', '日時形式、タイムゾーン、ログレベル、認証範囲を spring.smart.mvc で管理します。'],
      ['アプリケーションの主導権', 'Core は Spring に依存せず、主要な Bean と認証処理はアプリケーション側で置き換えられます。'],
    ],
    capabilitiesOverline: '主な機能', capabilitiesTitle: '今必要な機能から\n始められます。', capabilitiesDescription: 'それぞれの機能に独立した入口と明確な境界があります。順番に学ぶことも、今のプロジェクトに必要な機能へ直接進むこともできます。', featureLabels: ['統一レスポンス', '例外処理', '日時', '認証・認可', 'リクエストログ'], featureLink: '詳しく見る', catalogLink: 'ドキュメントをすべて見る',
    features: [
      ['レスポンスと自動ラップ', '通常のコントローラー戻り値を ApiResponse で包み、ApiResponse、byte[]、Resource、StreamingResponseBody、ProblemDetail はそのまま返します。'],
      ['例外と検証', 'HTTP エラー、業務エラー、Bean Validation を予測可能な構造に統一します。'],
      ['日時処理', 'LocalDate、Instant、OffsetDateTime、ZonedDateTime、Date を一貫して扱います。'],
      ['認証・認可', 'GLOBAL、ANNOTATED、ロール、権限、METHOD:PATH ルールに対応します。'],
      ['リクエストログ', '実際のコントローラーをロガーカテゴリとして、ステータスと処理時間を記録します。'],
    ],
    authOverline: '現在の認証情報', authTitle: 'どの Spring Bean からでも\n現在のユーザーを取得。', authDescription: 'CurrentAuth は注入可能なシングルトンの窓口です。認証情報は同期 Servlet リクエストごとに分離され、処理後に消去されます。', authLink: '認証・認可を学ぶ', identityLabel: 'リクエストごとの認証情報分離図', singletonLabel: 'Spring シングルトン', facadeOnly: 'アクセス窓口のみ', anonymous: '匿名リクエスト', noIdentity: '認証情報を作成しない', isolated: 'リクエストごとに分離',
    codeOverline: '使い慣れた書き方のまま', codeTitle: 'Controller は Controller のまま。', codeDescription: 'フレームワークの基底クラスも、戻り値ごとの手動ラッピングも必要ありません。', benefits: ['SmartMVC の依存関係を一つ追加するだけ', 'すべての設定を IDE で補完', '主要な拡張ポイントを置き換え可能'], copy: 'コピー', copied: 'コピーしました', codeLabel: 'コード例',
    closingOverline: '基礎から始める', closingTitle: 'まず一つの API を動かし、必要な機能を順に学びましょう。', closingAction: 'クイックスタートへ', footer: '一貫性があり、保守しやすい Spring MVC プロジェクトへ。',
  },
  ru: {
    heroLine1: 'Нужное берём на себя.', heroLine2: 'Решения — за вами.',
    heroDescription: 'SmartMVC объединяет единые ответы, обработку ошибок, дату и время, журналирование запросов, аутентификацию и авторизацию. Он не вмешивается в бизнес-логику, не требует базовых классов и не навязывает версии Spring Web или Bean Validation. Вы продолжаете писать привычный Spring MVC.',
    primary: 'Начать отсюда', secondary: 'Все разделы', ideMetadata: 'Автодополнение настроек в IDE', springFree: 'Core не зависит от Spring', boundary: 'Берём на себя повторение, а решения оставляем вам',
    traceLabel: 'Пример обработки защищённого запроса SmartMVC', traceHandler: 'Контроллер найден', traceIdentity: 'Правила аутентификации проверены', traceResponse: 'Ответ приведён к единому виду',
    principlesOverline: 'ВОЗМОЖНОСТИ И ГРАНИЦЫ', principlesTitle: 'Повторяющееся отдайте фреймворку.\nВажные решения оставьте себе.',
    principles: [
      ['Стабильный контракт API', 'Успешные ответы, ошибки и сведения о проверке полей имеют предсказуемую структуру.'],
      ['Настройки MVC в одном месте', 'Форматы дат, часовые пояса, уровни журналирования и область аутентификации находятся в spring.smart.mvc.'],
      ['Контроль остаётся у приложения', 'Core не зависит от Spring, а ключевые бины и процесс аутентификации можно заменить.'],
    ],
    capabilitiesOverline: 'ОСНОВНЫЕ ВОЗМОЖНОСТИ', capabilitiesTitle: 'Начните с того,\nчто нужно сейчас.', capabilitiesDescription: 'У каждой возможности своя точка входа и понятная граница. Идите по порядку или сразу переходите к тому, что нужно вашему приложению.', featureLabels: ['ЕДИНЫЙ ОТВЕТ', 'ОБРАБОТКА ОШИБОК', 'ДАТА И ВРЕМЯ', 'АУТЕНТИФИКАЦИЯ', 'ЖУРНАЛ ЗАПРОСОВ'], featureLink: 'Подробнее', catalogLink: 'Вся документация',
    features: [
      ['Ответы и упаковка', 'Обычные результаты контроллера преобразуются в ApiResponse; ApiResponse, byte[], Resource, StreamingResponseBody и ProblemDetail возвращаются без изменений.'],
      ['Ошибки и валидация', 'HTTP-ошибки, бизнес-ошибки и Bean Validation используют единую предсказуемую структуру.'],
      ['Дата и время', 'Единая работа с LocalDate, Instant, OffsetDateTime, ZonedDateTime и Date.'],
      ['Аутентификация и авторизация', 'Режимы GLOBAL и ANNOTATED, роли, именованные разрешения и правила METHOD:PATH.'],
      ['Журнал запросов', 'Категорией логгера становится фактический контроллер; записываются статус и время обработки.'],
    ],
    authOverline: 'ТЕКУЩАЯ АУТЕНТИФИКАЦИЯ', authTitle: 'Текущий пользователь доступен\nв любом Spring-бине.', authDescription: 'CurrentAuth — внедряемый синглтон-фасад. Данные пользователя изолированы в рамках синхронного Servlet-запроса и очищаются после обработки.', authLink: 'Об аутентификации', identityLabel: 'Схема изоляции пользователей по запросам', singletonLabel: 'СИНГЛТОН SPRING', facadeOnly: 'только фасад доступа', anonymous: 'Анонимный запрос', noIdentity: 'данные аутентификации не создаются', isolated: 'изолировано',
    codeOverline: 'ПРИВЫЧНАЯ МОДЕЛЬ', codeTitle: 'Контроллер остаётся обычным контроллером.', codeDescription: 'Не нужно наследовать базовый класс и вручную оборачивать каждый результат.', benefits: ['Только одна дополнительная зависимость SmartMVC', 'Автодополнение всех настроек в IDE', 'Ключевые точки расширения можно заменить'], copy: 'Копировать', copied: 'Скопировано', codeLabel: 'Примеры кода',
    closingOverline: 'НАЧНИТЕ С ОСНОВ', closingTitle: 'Запустите первый API, а затем изучайте возможности по мере необходимости.', closingAction: 'Перейти к быстрому старту', footer: 'Единообразные Spring MVC проекты, которые проще сопровождать.',
  },
} as const

const t = computed(() => messages[props.lang])
const prefix = computed(() => props.lang === 'zh' ? '' : `/${props.lang}`)
const snippets = {
  controller: `@GetMapping("/orders/{id}")\npublic OrderView get(@PathVariable Long id) {\n    return orderService.get(id);\n}`,
  auth: `@Auth(roles = "admin", permissions = "order:read")\n@GetMapping("/orders")\npublic List<OrderView> list() {\n    return orderService.list();\n}`,
  config: `spring:\n  smart:\n    mvc:\n      auth:\n        mode: GLOBAL\n        check-request-permission: true`,
}
const currentSnippet = computed(() => snippets[activeCode.value])
const featurePaths = [
  '/features/response.html',
  '/features/exceptions.html',
  '/features/date-time.html',
  '/auth/overview.html',
  '/features/request-logging.html',
] as const

function pageLink(path: string) {
  return withBase(`${prefix.value}${path}`)
}

async function copySnippet() {
  if (typeof navigator === 'undefined' || !navigator.clipboard) return
  await navigator.clipboard.writeText(currentSnippet.value)
  copied.value = true
  window.setTimeout(() => (copied.value = false), 1200)
}
</script>

<template>
  <div class="smart-landing" :data-locale="props.lang">
    <section class="landing-hero">
      <div class="hero-noise" aria-hidden="true" />
      <div class="hero-grid">
        <div class="hero-copy">
          <div class="release-line"><span class="release-pulse" /><span>SMARTMVC 1.0</span><i /><span>SPRING BOOT 3.2+</span></div>
          <h1>{{ t.heroLine1 }}<br><em>{{ t.heroLine2 }}</em></h1>
          <p class="hero-lede">{{ t.heroDescription }}</p>
          <div class="hero-actions">
            <a class="button button--solid" :href="pageLink('/guide/introduction.html')">{{ t.primary }}<span aria-hidden="true">↗</span></a>
            <a class="button button--quiet" :href="pageLink('/contents.html')">{{ t.secondary }}</a>
          </div>
          <div class="hero-footnote"><span>Java 17+</span><span>{{ t.ideMetadata }}</span><span>{{ t.springFree }}</span></div>
        </div>
        <div class="request-console" role="img" :aria-label="t.traceLabel">
          <div class="console-topbar"><div class="console-lights" aria-hidden="true"><i /><i /><i /></div><span>request.trace</span><b>LIVE</b></div>
          <div class="console-request"><span class="method">GET</span><code>/api/orders/1001</code><span class="duration">18 ms</span></div>
          <div class="trace-list">
            <div class="trace-row trace-row--done"><span class="trace-index">01</span><div><strong>{{ t.traceHandler }}</strong><small>OrderController#get</small></div><b>✓</b></div>
            <div class="trace-row trace-row--done"><span class="trace-index">02</span><div><strong>{{ t.traceIdentity }}</strong><small>role: admin · GET:/api/orders/**</small></div><b>✓</b></div>
            <div class="trace-row trace-row--active"><span class="trace-index">03</span><div><strong>{{ t.traceResponse }}</strong><small>ApiResponse&lt;OrderView&gt;</small></div><b><i /></b></div>
          </div>
          <div class="response-preview"><div class="response-head"><span>HTTP/1.1</span><strong>200 OK</strong></div><pre><code><span>{</span>
  <i>"success"</i>: <b>true</b>,
  <i>"code"</i>: <em>"OK"</em>,
  <i>"message"</i>: <em>"success"</em>,
  <i>"data"</i>: { <i>"id"</i>: <em>"1001"</em> },
  <i>"timestamp"</i>: <em>"1786006933924"</em>
<span>}</span></code></pre></div>
          <div class="console-glow" aria-hidden="true" />
        </div>
      </div>
      <div class="hero-rule"><span>{{ t.boundary }}</span><i /><span>01 — 05</span></div>
    </section>

    <section class="principles section-shell">
      <div class="section-heading"><p class="overline">{{ t.principlesOverline }}</p><h2>{{ t.principlesTitle }}</h2></div>
      <div class="principle-grid">
        <article v-for="(item, index) in t.principles" :key="item[0]"><span>0{{ index + 1 }}</span><h3>{{ item[0] }}</h3><p>{{ item[1] }}</p></article>
      </div>
    </section>

    <section class="capabilities section-shell">
      <div class="capability-intro"><p class="overline">{{ t.capabilitiesOverline }}</p><h2>{{ t.capabilitiesTitle }}</h2><p>{{ t.capabilitiesDescription }}</p><a class="catalog-link" :href="pageLink('/contents.html')">{{ t.catalogLink }} <span aria-hidden="true">→</span></a></div>
      <div class="capability-stack">
        <a v-for="(feature, index) in t.features" :key="feature[0]" class="capability-card" :class="{ 'capability-card--response': index === 0 }" :href="pageLink(featurePaths[index])" :aria-label="`${feature[0]} — ${t.featureLink}`">
          <div class="card-number">0{{ index + 1 }}</div>
          <div class="card-copy"><p>{{ t.featureLabels[index] }}</p><h3>{{ feature[0] }}</h3><span class="card-description">{{ feature[1] }}</span><span class="card-link">{{ t.featureLink }} <b aria-hidden="true">↗</b></span></div>
          <div v-if="index === 0" class="response-contract" aria-hidden="true"><b>2xx</b><i /><code>success</code><code>code</code><code>message</code><code>data</code><code>timestamp</code></div>
          <div v-else-if="index === 1" class="status-rail" aria-hidden="true"><span>400</span><span>401</span><span>403</span><span>404</span><span>422</span><span>503</span></div>
          <div v-else-if="index === 2" class="time-readout" aria-hidden="true"><strong>11:02</strong><span>:13</span><small>Asia/Shanghai</small></div>
          <div v-else-if="index === 3" class="auth-chip" aria-hidden="true"><i /><span>CurrentAuth</span><b>{{ t.isolated }}</b></div>
          <div v-else class="log-line" aria-hidden="true"><span>INFO</span><code>OrderController</code><b>18 ms</b></div>
        </a>
      </div>
    </section>

    <section class="auth-story">
      <div class="section-shell auth-grid">
        <div class="auth-copy"><p class="overline">{{ t.authOverline }}</p><h2>{{ t.authTitle }}</h2><p>{{ t.authDescription }}</p><a :href="pageLink('/auth/overview.html')">{{ t.authLink }} <span>→</span></a></div>
        <div class="identity-map" role="img" :aria-label="t.identityLabel">
          <div class="bean-node"><span>{{ t.singletonLabel }}</span><strong>CurrentAuth</strong><small>{{ t.facadeOnly }}</small></div>
          <div class="identity-lines" aria-hidden="true"><i /><i /><i /></div>
          <div class="request-node request-node--a"><span>REQ / 01</span><strong>Ada</strong><small>admin · order:read</small></div>
          <div class="request-node request-node--b"><span>REQ / 02</span><strong>Lin</strong><small>user · profile:read</small></div>
          <div class="request-node request-node--c"><span>REQ / 03</span><strong>{{ t.anonymous }}</strong><small>{{ t.noIdentity }}</small></div>
        </div>
      </div>
    </section>

    <section class="code-story section-shell">
      <div class="code-copy"><p class="overline">{{ t.codeOverline }}</p><h2>{{ t.codeTitle }}</h2><p>{{ t.codeDescription }}</p><ul><li v-for="benefit in t.benefits" :key="benefit">{{ benefit }}</li></ul></div>
      <div class="code-workbench">
        <div class="workbench-tabs" role="group" :aria-label="t.codeLabel">
          <button :aria-pressed="activeCode === 'controller'" :class="{ active: activeCode === 'controller' }" @click="activeCode = 'controller'">Controller</button>
          <button :aria-pressed="activeCode === 'auth'" :class="{ active: activeCode === 'auth' }" @click="activeCode = 'auth'">@Auth</button>
          <button :aria-pressed="activeCode === 'config'" :class="{ active: activeCode === 'config' }" @click="activeCode = 'config'">YAML</button>
          <button class="copy-button" aria-live="polite" @click="copySnippet">{{ copied ? t.copied : t.copy }}</button>
        </div>
        <pre><code>{{ currentSnippet }}</code></pre>
        <div class="workbench-result"><span>→</span><code>{ success: true, code: "OK", data: … }</code></div>
      </div>
    </section>

    <section class="closing-section section-shell">
      <div class="closing-card"><div><p class="overline">{{ t.closingOverline }}</p><h2>{{ t.closingTitle }}</h2></div><a :href="pageLink('/guide/getting-started.html')"><span>{{ t.closingAction }}</span><b>↗</b></a></div>
      <footer class="landing-footer"><strong>SmartMVC</strong><span>{{ t.footer }}</span><small>AGPL-3.0 · 2026</small></footer>
    </section>
  </div>
</template>

<style scoped>
.smart-landing{--landing-ink:#111b1a;--landing-muted:#63706d;--landing-paper:#f6f7f2;--landing-line:rgba(17,27,26,.14);--landing-mint:#a8f0cf;--landing-green:#2f8c68;position:relative;overflow:hidden;background:var(--landing-paper);color:var(--landing-ink)}
.section-shell{width:min(1180px,calc(100% - 48px));margin-inline:auto}.overline{margin:0;color:var(--landing-green);font:700 11px/1.2 var(--font-family-code);letter-spacing:.17em;text-transform:uppercase}.landing-hero{position:relative;min-height:calc(100vh - var(--navbar-height));padding:clamp(70px,9vw,126px) 24px 0;background:#101918;color:#eff7f3}.hero-noise{position:absolute;inset:0;pointer-events:none;opacity:.22;background-image:radial-gradient(circle at 24% 16%,rgba(168,240,207,.13),transparent 28%),radial-gradient(circle at 84% 78%,rgba(75,139,255,.12),transparent 30%),linear-gradient(rgba(255,255,255,.022) 1px,transparent 1px),linear-gradient(90deg,rgba(255,255,255,.022) 1px,transparent 1px);background-size:auto,auto,48px 48px,48px 48px}.hero-grid{position:relative;display:grid;grid-template-columns:minmax(0,1.05fr) minmax(420px,.95fr);gap:clamp(50px,8vw,110px);align-items:center;width:min(1180px,100%);margin:auto}.release-line{display:flex;align-items:center;gap:10px;margin-bottom:36px;color:#8fa39d;font:650 10px/1 var(--font-family-code);letter-spacing:.13em}.release-line i{width:1px;height:12px;background:#40504c}.release-pulse{width:7px;height:7px;border-radius:50%;background:var(--landing-mint);box-shadow:0 0 0 5px rgba(168,240,207,.08),0 0 18px rgba(168,240,207,.5)}.hero-copy h1{max-width:760px;margin:0;border:0;color:#f3f8f5;font-size:clamp(46px,5vw,68px);font-weight:640;line-height:1.02;letter-spacing:-.055em}.smart-landing[data-locale='zh'] .hero-copy h1{font-size:clamp(42px,4vw,52px);letter-spacing:-.045em}.smart-landing[data-locale='ja'] .hero-copy h1{font-size:clamp(38px,3.6vw,44px);letter-spacing:-.04em;line-height:1.12}.smart-landing[data-locale='ru'] .hero-copy h1{font-size:clamp(44px,4.7vw,58px)}.hero-copy h1 em{color:var(--landing-mint);font-style:normal;font-weight:640}.hero-lede{max-width:620px;margin:32px 0 0;color:#9eb0aa;font-size:clamp(16px,1.5vw,19px);line-height:1.75}.hero-actions{display:flex;flex-wrap:wrap;gap:10px;margin-top:38px}.button{display:inline-flex;align-items:center;justify-content:center;gap:26px;padding:14px 18px;border:1px solid transparent;border-radius:2px;font-size:13px;font-weight:690;text-decoration:none!important;transition:transform .2s ease,background .2s ease}.button:hover{transform:translateY(-2px)}.button--solid{background:var(--landing-mint);color:#0d1916}.button--quiet{border-color:#40514c;color:#d7e2dd}.button--quiet:hover{background:#192724}.hero-footnote{display:flex;flex-wrap:wrap;gap:8px 24px;margin-top:42px;color:#778a84;font:560 10px/1.2 var(--font-family-code);letter-spacing:.06em}.hero-footnote span{display:flex;align-items:center;gap:8px}.hero-footnote span:before{content:"";width:3px;height:3px;border-radius:50%;background:#557068}.request-console{position:relative;overflow:hidden;border:1px solid #344640;border-radius:5px;background:rgba(9,16,15,.82);box-shadow:0 45px 100px rgba(0,0,0,.32);font-family:var(--font-family-code);transform:translateY(12px)}.console-topbar{display:flex;align-items:center;height:46px;padding:0 16px;border-bottom:1px solid #293a35;color:#6f837d;font-size:10px;letter-spacing:.08em}.console-lights{display:flex;gap:5px;margin-right:12px}.console-lights i{width:6px;height:6px;border-radius:50%;background:#3b4c47}.console-topbar b{margin-left:auto;color:#83d9b4;font-size:9px}.console-request{display:grid;grid-template-columns:auto 1fr auto;gap:12px;align-items:center;padding:20px;border-bottom:1px solid #263730;font-size:11px}.console-request .method{padding:5px 7px;border-radius:2px;background:rgba(168,240,207,.12);color:#a8f0cf;font-weight:750}.console-request code{color:#b7c5c0}.duration{color:#688079}.trace-list{padding:12px 20px}.trace-row{position:relative;display:grid;grid-template-columns:26px 1fr auto;gap:12px;align-items:center;padding:12px 0;color:#7f928c}.trace-row:not(:last-child):after{content:"";position:absolute;left:12px;top:34px;bottom:-8px;width:1px;background:#2c4039}.trace-index{z-index:1;display:grid;place-items:center;width:24px;height:24px;border:1px solid #354b43;border-radius:50%;background:#0d1715;color:#6e847c;font-size:8px}.trace-row div{display:flex;flex-direction:column;gap:3px}.trace-row strong{color:#c9d6d1;font-size:10px;font-weight:620}.trace-row small{color:#5e746d;font-size:8px}.trace-row>b{color:#84dcb7;font-size:10px}.trace-row--active .trace-index{border-color:#79cfa9;color:#a8f0cf;box-shadow:0 0 18px rgba(168,240,207,.1)}.trace-row--active>b{width:8px;height:8px;border:1px solid #79cfa9;border-radius:50%;animation:pulse 1.5s ease-in-out infinite}.response-preview{margin:4px 20px 20px;border:1px solid #2c4039;border-radius:3px;background:#0a1211}.response-head{display:flex;justify-content:space-between;padding:9px 11px;border-bottom:1px solid #263730;color:#61766f;font-size:8px}.response-head strong{color:#8de1bd}.response-preview pre{margin:0;padding:13px 15px;background:transparent;color:#7f9990;font-size:9px;line-height:1.65}.response-preview i{color:#84b9ee;font-style:normal}.response-preview b{color:#a8f0cf}.response-preview em{color:#e2ba82;font-style:normal}.console-glow{position:absolute;right:-90px;bottom:-110px;width:250px;height:250px;border-radius:50%;background:rgba(89,190,148,.08);filter:blur(45px)}.hero-rule{position:relative;display:flex;align-items:center;gap:18px;width:min(1180px,100%);margin:clamp(68px,9vw,112px) auto 0;padding:18px 0;border-top:1px solid #33443f;color:#667a73;font:600 9px/1 var(--font-family-code);letter-spacing:.12em}.hero-rule i{flex:1;height:1px;background:linear-gradient(90deg,#33443f,transparent)}.principles{padding:clamp(100px,12vw,170px) 0}.section-heading{display:grid;grid-template-columns:.85fr 1.15fr;gap:40px;align-items:start}.section-heading h2,.capability-intro h2,.auth-copy h2,.code-copy h2,.closing-card h2{margin:0;border:0;white-space:pre-line;font-size:clamp(36px,5vw,66px);font-weight:610;line-height:1.05;letter-spacing:-.055em}.principle-grid{display:grid;grid-template-columns:repeat(3,1fr);margin-top:80px;border-top:1px solid var(--landing-line);border-bottom:1px solid var(--landing-line)}.principle-grid article{min-height:260px;padding:28px 30px 34px;border-right:1px solid var(--landing-line)}.principle-grid article:first-child{padding-left:0}.principle-grid article:last-child{border-right:0}.principle-grid article>span,.card-number{color:#8b9692;font:600 10px/1 var(--font-family-code)}.principle-grid h3{margin:74px 0 14px;border:0;font-size:20px;letter-spacing:-.025em}.principle-grid p{margin:0;color:var(--landing-muted);font-size:14px;line-height:1.75}.capabilities{display:grid;grid-template-columns:360px 1fr;gap:clamp(50px,8vw,110px);padding:0 0 clamp(110px,13vw,180px)}.capability-intro{position:sticky;top:calc(var(--navbar-height) + 40px);align-self:start}.capability-intro h2{margin-top:22px;font-size:clamp(36px,4.3vw,58px)}.capability-intro>p:not(.overline){margin-top:25px;color:var(--landing-muted);font-size:14px;line-height:1.8}.catalog-link{display:inline-flex;align-items:center;gap:24px;margin-top:20px;color:var(--landing-green);font-size:12px;font-weight:700;text-decoration:none!important}.catalog-link span{transition:transform .2s ease}.catalog-link:hover span{transform:translateX(4px)}.capability-stack{border-top:1px solid var(--landing-line)}.capability-card{position:relative;display:grid;grid-template-columns:40px 1fr;gap:16px;min-height:190px;padding:30px 0;border-bottom:1px solid var(--landing-line);color:inherit;text-decoration:none!important;transition:background-color .2s ease}.capability-card:hover{background:rgba(47,140,104,.045)}.capability-card:hover .card-copy h3{color:var(--landing-green)}.card-copy p{margin:0;color:var(--landing-green);font:700 9px/1 var(--font-family-code);letter-spacing:.14em}.card-copy h3{margin:11px 0 12px;border:0;font-size:24px;letter-spacing:-.035em;transition:color .2s ease}.card-description{display:block;max-width:560px;color:var(--landing-muted);font-size:13px;line-height:1.7}.card-link{display:inline-flex;align-items:center;gap:10px;margin-top:16px;color:var(--landing-green);font-size:11px;font-weight:700;line-height:1.4}.card-link b{font-size:13px;transition:transform .2s ease}.capability-card:hover .card-link b{transform:translate(2px,-2px)}.response-contract{display:flex;align-items:center;gap:8px;margin:24px 0 0 56px}.response-contract b{color:var(--landing-green);font:700 10px var(--font-family-code)}.response-contract i{width:20px;height:1px;background:var(--landing-line)}.response-contract code{padding:5px 7px;border:1px solid var(--landing-line);border-radius:2px;background:rgba(255,255,255,.45);color:#66726e;font-size:8px}.status-rail{display:flex;gap:4px;margin:26px 0 0 56px}.status-rail span{padding:5px 9px;border-radius:20px;background:#e8ebe5;color:#727e7a;font:600 8px var(--font-family-code)}.time-readout{display:flex;align-items:baseline;margin:22px 0 0 56px;font-family:var(--font-family-code)}.time-readout strong{font-size:32px;font-weight:520;letter-spacing:-.06em}.time-readout>span{color:var(--landing-green);font-size:18px}.time-readout small{margin-left:12px;color:#85908c;font-size:8px}.auth-chip,.log-line{display:flex;align-items:center;width:fit-content;margin:24px 0 0 56px;padding:8px 10px;border:1px solid var(--landing-line);border-radius:3px;background:rgba(255,255,255,.5);font:600 9px var(--font-family-code)}.auth-chip i{width:6px;height:6px;margin-right:8px;border-radius:50%;background:var(--landing-green);box-shadow:0 0 0 4px rgba(47,140,104,.08)}.auth-chip b{margin-left:18px;color:var(--landing-green);font-size:7px;text-transform:uppercase}.log-line{gap:12px}.log-line span{color:var(--landing-green)}.log-line code{color:#64716d}.log-line b{color:#8b9692;font-size:8px}.auth-story{padding:clamp(100px,12vw,160px) 0;background:#111b1a;color:#eff7f3}.auth-grid{display:grid;grid-template-columns:.85fr 1.15fr;gap:clamp(60px,9vw,130px);align-items:center}.auth-copy h2{margin-top:23px}.auth-copy>p:not(.overline){max-width:520px;margin:28px 0;color:#97aaa4;font-size:15px;line-height:1.8}.auth-copy>a{display:inline-flex;gap:28px;color:#a8f0cf;font-size:12px;font-weight:680;text-decoration:none}.identity-map{position:relative;min-height:440px;border:1px solid #34443f;background:linear-gradient(135deg,rgba(168,240,207,.035),transparent 46%)}.bean-node,.request-node{position:absolute;display:flex;flex-direction:column;border:1px solid #3b4c47;background:#15211f}.bean-node{top:50%;left:50%;z-index:2;width:190px;padding:23px;transform:translate(-50%,-50%);box-shadow:0 24px 60px rgba(0,0,0,.25)}.bean-node span,.request-node span{color:#688078;font:650 8px var(--font-family-code);letter-spacing:.12em}.bean-node strong,.request-node strong{margin-top:9px;color:#e5efeb;font-size:16px}.bean-node small,.request-node small{margin-top:5px;color:#72867f;font:500 8px var(--font-family-code)}.request-node{width:150px;padding:15px}.request-node--a{top:28px;left:28px}.request-node--b{right:28px;top:52px}.request-node--c{right:48px;bottom:34px}.identity-lines{position:absolute;inset:0}.identity-lines:before,.identity-lines:after,.identity-lines i{content:"";position:absolute;height:1px;transform-origin:left;background:#385048}.identity-lines:before{width:180px;left:128px;top:105px;transform:rotate(34deg)}.identity-lines:after{width:174px;left:55%;top:50%;transform:rotate(-49deg)}.identity-lines i:nth-child(1){width:150px;left:50%;top:50%;transform:rotate(-147deg)}.identity-lines i:nth-child(2){width:145px;left:50%;top:50%;transform:rotate(-33deg)}.identity-lines i:nth-child(3){width:170px;left:50%;top:50%;transform:rotate(35deg)}.code-story{display:grid;grid-template-columns:.82fr 1.18fr;gap:clamp(50px,8vw,110px);align-items:center;padding:clamp(110px,13vw,180px) 0}.code-copy h2{margin-top:22px;font-size:clamp(38px,4.6vw,60px)}.code-copy>p:not(.overline){margin:25px 0;color:var(--landing-muted);font-size:14px;line-height:1.8}.code-copy ul{display:grid;gap:10px;margin:28px 0 0;padding:0;list-style:none}.code-copy li{display:flex;align-items:center;gap:10px;color:#4f5d59;font-size:12px}.code-copy li:before{content:"";width:5px;height:5px;border-radius:50%;background:var(--landing-green)}.code-workbench{overflow:hidden;border:1px solid #2e3d39;border-radius:4px;background:#101918;box-shadow:0 35px 80px rgba(17,27,26,.17)}.workbench-tabs{display:flex;align-items:center;gap:2px;padding:10px;border-bottom:1px solid #293a35}.workbench-tabs button{padding:8px 10px;border:0;border-radius:2px;background:transparent;color:#71847e;cursor:pointer;font:600 9px var(--font-family-code)}.workbench-tabs button.active{background:#1a2925;color:#a8f0cf}.workbench-tabs .copy-button{margin-left:auto}.code-workbench pre{min-height:200px;margin:0;padding:28px;background:transparent;color:#bfd0ca;font-size:12px;line-height:1.85}.workbench-result{display:flex;align-items:center;gap:15px;padding:15px 20px;border-top:1px solid #293a35;background:#0d1514;color:#7e948d;font-size:9px}.workbench-result>span{color:#a8f0cf}.workbench-result code{color:#8fbfa9}.closing-section{padding-bottom:32px}.closing-card{display:flex;align-items:flex-end;justify-content:space-between;gap:40px;padding:clamp(42px,7vw,80px);background:#a8f0cf}.closing-card h2{max-width:800px;margin-top:20px;font-size:clamp(38px,5vw,64px)}.closing-card>a{display:flex;align-items:center;gap:30px;flex:0 0 auto;padding:14px 16px;border:1px solid rgba(17,27,26,.32);color:#111b1a;font-size:12px;font-weight:700;text-decoration:none}.closing-card>a b{font-size:18px}.landing-footer{display:grid;grid-template-columns:1fr 1fr auto;gap:30px;align-items:center;padding:32px 0;color:#697571;font-size:10px}.landing-footer strong{color:#24302d;font-size:14px}.landing-footer small{font-family:var(--font-family-code)}
@keyframes pulse{50%{opacity:.35;transform:scale(.7)}}
@media (max-width:900px){.hero-grid,.auth-grid,.code-story{grid-template-columns:1fr}.hero-grid{gap:70px}.request-console{width:min(620px,100%)}.capabilities{grid-template-columns:1fr}.capability-intro{position:static}.section-heading{grid-template-columns:1fr}.principle-grid{grid-template-columns:1fr}.principle-grid article{min-height:auto;padding:28px 0;border-right:0;border-bottom:1px solid var(--landing-line)}.principle-grid article:last-child{border-bottom:0}.principle-grid h3{margin-top:35px}.identity-map{min-height:430px}}
@media (max-width:600px){.section-shell{width:min(100% - 32px,1180px)}.landing-hero{padding-inline:16px}.hero-copy h1{font-size:42px}.smart-landing[data-locale='zh'] .hero-copy h1,.smart-landing[data-locale='ja'] .hero-copy h1{font-size:clamp(29px,8.5vw,34px);line-height:1.12}.smart-landing[data-locale='ru'] .hero-copy h1{font-size:38px}.hero-actions{align-items:stretch;flex-direction:column}.button{justify-content:space-between}.hero-footnote{align-items:flex-start;flex-direction:column}.request-console{transform:none}.console-request{grid-template-columns:auto 1fr}.duration{display:none}.trace-row small{overflow:hidden;max-width:190px;text-overflow:ellipsis;white-space:nowrap}.response-preview{margin-inline:12px}.principles{padding-block:90px}.principle-grid{margin-top:50px}.capabilities{padding-bottom:90px}.capability-card{grid-template-columns:30px 1fr;padding-inline:8px}.response-contract,.status-rail,.time-readout,.auth-chip,.log-line{margin-left:46px;flex-wrap:wrap}.auth-story{padding-block:90px}.identity-map{min-height:520px}.bean-node{top:48%;width:160px}.request-node{width:125px}.request-node--a{top:20px;left:20px}.request-node--b{right:20px;top:110px}.request-node--c{right:25px;bottom:25px}.identity-lines{opacity:.5}.code-story{padding-block:90px}.code-workbench pre{min-height:180px;padding:20px;font-size:10px}.closing-card{align-items:flex-start;flex-direction:column;padding:34px 24px}.closing-card>a{width:100%;box-sizing:border-box;justify-content:space-between}.landing-footer{grid-template-columns:1fr;gap:8px}.landing-footer span{order:3}}
@media (prefers-reduced-motion:reduce){.trace-row--active>b{animation:none}.button,.catalog-link span,.capability-card,.card-copy h3,.card-link b{transition:none}}
:global([data-theme='dark']) .smart-landing{--landing-ink:#e9f1ed;--landing-muted:#8fa19b;--landing-paper:#0c1312;--landing-line:rgba(218,235,227,.12);--landing-green:#84dcb7}.smart-landing :global(a:focus-visible),.smart-landing button:focus-visible{outline:2px solid #7ed9b3;outline-offset:3px}
</style>
