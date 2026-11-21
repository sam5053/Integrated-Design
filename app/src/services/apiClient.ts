const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? '').replace(/\/$/, '')

export const USE_MOCK = import.meta.env.VITE_USE_MOCK !== 'false'

export function postJson<T>(
  path: string,
  data: UniApp.RequestOptions['data'],
): Promise<T> {
  if (!API_BASE_URL) {
    return Promise.reject(new Error('请先在 .env.local 中配置后端地址'))
  }

  return new Promise((resolve, reject) => {
    uni.request({
      url: `${API_BASE_URL}${path}`,
      method: 'POST',
      data,
      header: {
        'Content-Type': 'application/json; charset=UTF-8',
      },
      success(response) {
        if (response.statusCode >= 200 && response.statusCode < 300) {
          resolve(response.data as T)
          return
        }

        reject(new Error(`请求失败：${response.statusCode}`))
      },
      fail() {
        reject(new Error('网络请求失败，请检查后端地址和网络'))
      },
    })
  })
}
