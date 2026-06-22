'use client'

import { useState, useEffect, useCallback } from 'react'
import {api, UrlEntry} from "@/app/lib/api";
import ShortenForm from "@/components/ShortenForm";
import UrlTable from "@/components/UrlTable";



export default function UrlShortenerApp() {
    const [urls, setUrls] = useState<UrlEntry[]>([])

    const loadUrls = useCallback(async () => {
        try {
            const data = await api.listAll()
            setUrls(data)
        } catch {
            // silently fail on initial load
        }
    }, [])

    useEffect(() => { loadUrls() }, [loadUrls])

    const handleDelete = async (alias: string) => {
        await api.deleteAlias(alias)
        setUrls((prev) => prev.filter((u) => u.alias !== alias))
    }

    return (
        <>
            <ShortenForm onSuccess={loadUrls} />
            <UrlTable urls={urls} onDelete={handleDelete} />
        </>
    )
}