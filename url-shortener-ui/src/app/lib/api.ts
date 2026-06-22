export interface UrlEntry {
    alias: string
    fullUrl: string
    shortUrl: string
}

const API_BASE =
    typeof window === 'undefined'
        ? (process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080')
        : (process.env.NEXT_PUBLIC_API_URL ?? '')

async function handleResponse<T>(res: Response): Promise<T> {
    if (!res.ok) {
        const body = await res.json().catch(() => ({ error: `HTTP ${res.status}` }))
        throw new Error(body.error ?? `HTTP ${res.status}`)
    }
    return res.json() as Promise<T>
}

export const api = {
    shorten: (fullUrl: string, customAlias?: string) =>
        fetch(`${API_BASE}/shorten`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ fullUrl, ...(customAlias ? { customAlias } : {}) }),
        }).then((res) => handleResponse<{ shortUrl: string }>(res)),

    listAll: () =>
        fetch(`${API_BASE}/urls`).then((res) => handleResponse<UrlEntry[]>(res)),

    deleteAlias: (alias: string) =>
        fetch(`${API_BASE}/${alias}`, { method: 'DELETE' }).then((res) => {
            if (!res.ok) throw new Error(`HTTP ${res.status}`)
        }),
}
