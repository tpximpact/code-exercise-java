'use client'

import { useState } from 'react'
import {UrlEntry} from "@/app/lib/api";


interface UrlTableProps {
    urls: UrlEntry[]
    onDelete: (alias: string) => Promise<void>
}

export default function UrlTable({ urls, onDelete }: UrlTableProps) {
    const [copied, setCopied] = useState<string | null>(null)
    const [deletingAlias, setDeletingAlias] = useState<string | null>(null)

    const handleCopy = async (url: string) => {
        await navigator.clipboard.writeText(url)
        setCopied(url)
        setTimeout(() => setCopied(null), 2000)
    }

    const handleDelete = async (alias: string) => {
        setDeletingAlias(alias)
        try {
            await onDelete(alias)
        } finally {
            setDeletingAlias(null)
        }
    }

    if (urls.length === 0) return null

    return (
        <section className="flex flex-col gap-3">
            {/* Header row */}
            <div className="flex items-center justify-between">
        <span className="font-mono text-[0.6rem] tracking-[0.14em] uppercase text-accent font-medium">
          All Links
        </span>
                <span className="font-mono text-[0.65rem] text-muted">
          {urls.length} {urls.length === 1 ? 'entry' : 'entries'}
        </span>
            </div>

            {/* Table */}
            <ul
                aria-label="All shortened URLs"
                className="border-2 border-ink shadow-brutal bg-card divide-y divide-border"
            >
                {urls.map((entry, i) => (
                    <li
                        key={entry.alias}
                        className="flex items-center justify-between gap-4 px-5 py-3.5 hover:bg-paper/60 transition-colors group"
                        style={{ animationDelay: `${i * 40}ms` }}
                    >
                        {/* Left: alias + full url */}
                        <div className="flex flex-col gap-0.5 min-w-0 flex-1">
              <span className="font-mono text-sm font-medium text-accent group-hover:text-accent-light transition-colors">
                /{entry.alias}
              </span>
                            <span
                                className="font-mono text-[0.7rem] text-muted truncate"
                                title={entry.fullUrl}
                            >
                {entry.fullUrl}
              </span>
                        </div>

                        {/* Actions */}
                        <div className="flex items-center gap-2 shrink-0">
                            <button
                                onClick={() => handleCopy(entry.shortUrl)}
                                aria-label={`Copy short URL for ${entry.alias}`}
                                className="
                  w-8 h-8 flex items-center justify-center text-sm
                  border-[1.5px] border-border text-ink
                  hover:border-emerald-500 hover:text-emerald-600
                  transition-colors duration-100
                "
                            >
                                {copied === entry.shortUrl ? '✓' : '⎘'}
                            </button>
                            <button
                                onClick={() => handleDelete(entry.alias)}
                                disabled={deletingAlias === entry.alias}
                                aria-label={`Delete ${entry.alias}`}
                                className="w-8 h-8 flex items-center justify-center text-xs
                  border-[1.5px] border-border text-ink
                  hover:border-accent hover:text-accent hover:bg-red-50
                  disabled:opacity-40 disabled:cursor-not-allowed
                  transition-colors duration-100
                "
                            >
                                {deletingAlias === entry.alias ? '…' : '✕'}
                            </button>
                        </div>
                    </li>
                ))}
            </ul>
        </section>
    )
}