import { mkdir } from 'node:fs/promises'
import { join } from 'node:path'
import { fileURLToPath } from 'node:url'
import { chromium } from 'playwright-core'

const browserPath =
  process.env.BROWSER_PATH ?? 'C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe'
const baseUrl = process.env.PREVIEW_URL ?? 'http://localhost:5174/'
const screenshotDir = fileURLToPath(new URL('../screenshots/', import.meta.url))

await mkdir(screenshotDir, { recursive: true })

const browser = await chromium.launch({
  executablePath: browserPath,
  headless: true,
  args: ['--no-sandbox', '--disable-gpu', '--disable-dev-shm-usage'],
})

const context = await browser.newContext({
  viewport: { width: 390, height: 844 },
  deviceScaleFactor: 1,
  isMobile: true,
  hasTouch: true,
  locale: 'zh-CN',
})
const page = await context.newPage()
const pageErrors = []

page.on('pageerror', (error) => pageErrors.push(error.message))

try {
  await page.goto(baseUrl, { waitUntil: 'networkidle' })
  await assertNoHorizontalOverflow(page)
  await page.screenshot({
    path: join(screenshotDir, 'login-app.png'),
    fullPage: true,
  })

  await page.getByText('注册账户', { exact: true }).click()
  await page.getByText('确认密码', { exact: true }).waitFor()
  await page.getByLabel('用户名', { exact: true }).locator('input').fill('testuser')
  await page.getByLabel('手机号').locator('input').fill('13900000000')
  await page.getByLabel('密码', { exact: true }).locator('input').fill('123456')
  await page.getByLabel('确认密码', { exact: true }).locator('input').fill('123456')
  await page.screenshot({
    path: join(screenshotDir, 'register-app.png'),
    fullPage: true,
  })
  await page.locator('uni-button.button').click()
  await page.getByText('确认密码', { exact: true }).waitFor({
    state: 'detached',
    timeout: 10000,
  })

  await page.getByText('注册账户', { exact: true }).click()
  await page.getByText('确认密码', { exact: true }).waitFor()
  await page.getByLabel('用户名', { exact: true }).locator('input').fill('13800138000')
  await page.getByLabel('手机号').locator('input').fill('13900000001')
  await page.getByLabel('密码', { exact: true }).locator('input').fill('123456')
  await page.getByLabel('确认密码', { exact: true }).locator('input').fill('123456')
  await page.locator('uni-button.button').click()
  await page.getByText('用户名已存在', { exact: true }).waitFor({ timeout: 10000 })
  await page.screenshot({
    path: join(screenshotDir, 'register-username-exists.png'),
    fullPage: true,
  })

  await page.getByLabel('用户名', { exact: true }).locator('input').fill('testuser2')
  await page.getByLabel('手机号').locator('input').fill('13800138000')
  await page.locator('uni-button.button').click()
  await page.getByText('手机号已注册', { exact: true }).waitFor({ timeout: 10000 })
  await page.screenshot({
    path: join(screenshotDir, 'register-phone-exists.png'),
    fullPage: true,
  })
  await page.getByText('返回登录', { exact: true }).click()

  await page.getByLabel('用户名或手机号').waitFor()
  await page.getByLabel('用户名或手机号').locator('input').fill('testuser')
  await page.getByLabel('密码', { exact: true }).locator('input').fill('123456')
  await page.locator('uni-button.button').click()
  await page.getByText('常用功能', { exact: true }).waitFor({ timeout: 10000 })
  await page.getByText('你好，testuser', { exact: true }).waitFor()
  await page.screenshot({
    path: join(screenshotDir, 'home-app.png'),
    fullPage: true,
  })

  await page.getByText('实名认证', { exact: true }).click()
  await page.getByLabel('姓名').locator('input').fill('张三')
  await page.getByLabel('身份证号').locator('input').fill('110101199001011234')
  await page.screenshot({
    path: join(screenshotDir, 'identity-app.png'),
    fullPage: true,
  })
  await page.locator('uni-button.button').click()
  await page.getByText('已完成实名认证', { exact: true }).waitFor({
    timeout: 10000,
  })
  await page.screenshot({
    path: join(screenshotDir, 'home-verified.png'),
    fullPage: true,
  })

  await page.getByText('银行卡', { exact: true }).click()
  await page.getByText('已绑定银行卡', { exact: true }).waitFor()
  await page.getByText('还没有绑定银行卡', { exact: true }).waitFor({
    timeout: 10000,
  })
  await assertNoHorizontalOverflow(page)
  await page.screenshot({
    path: join(screenshotDir, 'bank-cards-empty.png'),
    fullPage: true,
  })

  await page.locator('uni-button.add-button').click()
  await page.getByLabel('开户银行').locator('input').fill('中国工商银行')
  await page.getByLabel('银行卡号').locator('input').fill('6222021234567890')
  await page.screenshot({
    path: join(screenshotDir, 'bank-card-bind.png'),
    fullPage: true,
  })
  await page.locator('uni-button.button').click()
  await page.getByText('中国工商银行', { exact: true }).waitFor({
    timeout: 10000,
  })
  await page.getByText('**** **** **** 7890', { exact: true }).waitFor()
  await assertNoHorizontalOverflow(page)
  await page.screenshot({
    path: join(screenshotDir, 'bank-cards-bound.png'),
    fullPage: true,
  })

  await page.reload({ waitUntil: 'networkidle' })
  await page.getByLabel('用户名或手机号').waitFor({ timeout: 10000 })

  if (pageErrors.length > 0) {
    throw new Error(`页面运行错误：${pageErrors.join(' | ')}`)
  }

  console.log('App 检查通过：注册、登录、实名认证、银行卡绑定、重新加载后重新登录。')
} finally {
  await context.close()
  await browser.close()
}

async function assertNoHorizontalOverflow(pageInstance) {
  const metrics = await pageInstance.evaluate(() => ({
    viewportWidth: window.innerWidth,
    documentWidth: document.documentElement.scrollWidth,
  }))

  if (metrics.documentWidth > metrics.viewportWidth) {
    throw new Error(
      `页面存在横向溢出：视口 ${metrics.viewportWidth}px，内容 ${metrics.documentWidth}px`,
    )
  }
}
