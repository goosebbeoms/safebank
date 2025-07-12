import axios from "axios";

const api = axios.create({
    baseURL: '/api',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json'
    }
})

// 요청 인터셉터 (향후 JWT 토큰 추가 사전 반영(주석 처리))
api.interceptors.request.use((config) => {
    const token = localStorage.getItem(('token'))
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }

    return config
})

// 응답 인터셉터 (에러 처리)
api.interceptors.response.use(
    (response) => response,
    (error) => {
        console.error('API Error : ', error)
        return Promise.reject(error)
    }
)

export default api;