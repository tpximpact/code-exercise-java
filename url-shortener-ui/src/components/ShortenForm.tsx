'use client'

import { useState } from 'react'
import {api} from "@/app/lib/api";


interface ShortenFormProps {
    onSuccess: () => void
}

export default function ShortenForm({ onSuccess }: ShortenFormProps) {
    const [fullUrl, setFullUrl] = useState('')
    const [customAlias, setCustomAlias] = useState('')
    const [result, setResult] = useState<string | null>(null)
    const [error, setError] = useState<string | null>(null)
    const [loading, setLoading] = useState(false)
    const [copied, setCopied] = useState(false)

    const handleShorten = async () => {
        if (!fullUrl.trim()) { setError('Please enter a URL'); return }
        setLoading(true)
        setError(null)
        setResult(null)
        try {
            const { shortUrl } = await api.shorten(fullUrl.trim(), customAlias.trim() || undefined)
            setResult(shortUrl)
            setFullUrl('')
            setCustomAlias('')
            onSuccess()
        } catch (e) {
            setError(e instanceof Error ? e.message : 'Something went wrong')
        } finally {
            setLoading(false)
        }
    }

    const handleCopy = async () => {
        if (!result) return
        await navigator.clipboard.writeText(result)
        setCopied(true)
        setTimeout(() => setCopied(false), 2000)
    }

    const handleKey = (e: React.KeyboardEvent) => {
        if (e.key === 'Enter') handleShorten()
    }

    return (
        <section className="bg-card border-2 border-ink shadow-brutal p-7 flex flex-col gap-5">
            {/* Section label */}
            <span className="font-mono text-[0.6rem] tracking-[0.14em] uppercase text-accent font-medium">
        New Link
      </span>

            {/* Full URL field */}
            <div className="flex flex-col gap-1.5">
                <label
                    htmlFor="fullUrl"
                    className="font-mono text-[0.7rem] tracking-widest uppercase text-muted font-medium"
                >
                    Long URL
                </label>
                <input
                    id="fullUrl"
                    type="url"
                    placeholder="https://your-very-long-url.com/goes/here"
                    value={fullUrl}
                    onChange={(e) => setFullUrl(e.target.value)}
                    onKeyDown={handleKey}
                    aria-label="Full URL to shorten"
                    className="
            font-mono text-sm px-4 py-3
            bg-paper border-[1.5px] border-border text-ink
            placeholder:text-muted/50
            focus:border-ink focus:outline-none
            transition-colors w-full
          "
                />
            </div>

            {/* Custom alias field */}
            <div className="flex flex-col gap-1.5">
                <label
                    htmlFor="alias"
                    className="font-mono text-[0.7rem] tracking-widest uppercase text-muted font-medium flex items-center gap-2"
                >
                    Custom Alias
                    <span className="bg-paper text-muted/70 text-[0.58rem] px-1.5 py-0.5 tracking-wide uppercase">
            optional
          </span>
                </label>
                <div className="flex">
          <span className="font-mono text-sm px-3 py-3 bg-ink text-paper flex items-center shrink-0 select-none">
            snip/
          </span>
                    <input
                        id="alias"
                        type="text"
                        placeholder="my-custom-alias"
                        value={customAlias}
                        onChange={(e) => setCustomAlias(e.target.value)}
                        onKeyDown={handleKey}
                        aria-label="Custom alias"
                        className="
              font-mono text-sm px-4 py-3 flex-1
              bg-paper border-[1.5px] border-l-0 border-border text-ink
              placeholder:text-muted/50
              focus:border-ink focus:outline-none
              transition-colors
            "
                    />
                </div>
            </div>

            {/* Submit */}
            <button
                onClick={handleShorten}
                disabled={loading}
                aria-busy={loading}
                className="
          self-start font-display font-bold text-base px-7 py-3
          bg-accent text-white border-2 border-accent
          hover:bg-accent-light hover:border-accent-light
          active:translate-x-[2px] active:translate-y-[2px]
          disabled:opacity-50 disabled:cursor-not-allowed
          transition-all duration-100
          flex items-center gap-2.5
        "
            >
                {loading ? (
                    <span
                        className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"
                        aria-label="Loading"
                    />
                ) : (
                    <>
                        Shorten
                        <span className="text-lg leading-none">→</span>
                    </>
                )}
            </button>

            {/* Error */}
            {error && (
                <div
                    role="alert"
                    className="animate-fade-up flex items-center gap-2.5 px-4 py-3 border-[1.5px] border-accent bg-red-50 text-accent text-sm font-mono"
                >
                    <span className="font-bold text-xs">✕</span>
                    {error}
                </div>
            )}

            {/* Success */}
            {result && (
                <div
                    role="status"
                    className="animate-fade-up flex items-center justify-between gap-3 px-4 py-3 border-[1.5px] border-emerald-600 bg-emerald-50 text-emerald-700"
                >
                    <div className="flex items-center gap-2 min-w-0">
                        <span className="font-mono text-xs font-bold shrink-0">✓</span>
                        <a
                            href={result}
                            target="_blank"
                            rel="noopener noreferrer"
                            className="font-mono text-sm font-medium truncate hover:underline"
                        >
                            {result}
                        </a>
                    </div>
                    <button
                        onClick={handleCopy}
                        aria-label="Copy short URL"
                        className="
              shrink-0 font-mono text-[0.7rem] uppercase tracking-wider px-3 py-1.5
              border-[1.5px] border-emerald-600 text-emerald-700
              hover:bg-emerald-600 hover:text-white
              transition-colors duration-100
            "
                    >
                        {copied ? 'Copied!' : 'Copy'}
                    </button>
                </div>
            )}
        </section>
    )
}