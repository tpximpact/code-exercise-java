import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import ShortenForm from '../components/ShortenForm'
import UrlTable from '../components/UrlTable'
import * as apiModule from '../app/lib/api'


vi.mock('../app/lib/api', () => ({
    api: {
        shorten: vi.fn(),
        listAll: vi.fn(),
        deleteAlias: vi.fn(),
    },
}))

const mockedApi = apiModule.api as {
    shorten: ReturnType<typeof vi.fn>
    listAll: ReturnType<typeof vi.fn>
    deleteAlias: ReturnType<typeof vi.fn>
}

// ── ShortenForm ───────────────────────────────────────────────────────────────

describe('ShortenForm', () => {
    const onSuccess = vi.fn()

    beforeEach(() => {
        vi.clearAllMocks()
    })

    it('renders URL input and alias input', () => {
        render(<ShortenForm onSuccess={onSuccess} />)
        expect(screen.getByLabelText('Full URL to shorten')).toBeInTheDocument()
        expect(screen.getByLabelText('Custom alias')).toBeInTheDocument()
    })

    it('shows validation error when submitting empty URL', async () => {
        render(<ShortenForm onSuccess={onSuccess} />)
        fireEvent.click(screen.getByRole('button', { name: /shorten/i }))
        await waitFor(() => {
            expect(screen.getByRole('alert')).toBeInTheDocument()
            expect(screen.getByText('Please enter a URL')).toBeInTheDocument()
        })
        expect(mockedApi.shorten).not.toHaveBeenCalled()
    })

    it('calls api.shorten and shows result on success', async () => {
        mockedApi.shorten.mockResolvedValueOnce({ shortUrl: 'http://localhost:8080/abc1234' })
        render(<ShortenForm onSuccess={onSuccess} />)

        fireEvent.change(screen.getByLabelText('Full URL to shorten'), {
            target: { value: 'https://example.com' },
        })
        fireEvent.click(screen.getByRole('button', { name: /shorten/i }))

        await waitFor(() => {
            expect(screen.getByText('http://localhost:8080/abc1234')).toBeInTheDocument()
        })
        expect(onSuccess).toHaveBeenCalledOnce()
    })

    it('calls api.shorten with custom alias when provided', async () => {
        mockedApi.shorten.mockResolvedValueOnce({ shortUrl: 'http://localhost:8080/my-alias' })
        render(<ShortenForm onSuccess={onSuccess} />)

        fireEvent.change(screen.getByLabelText('Full URL to shorten'), {
            target: { value: 'https://example.com' },
        })
        fireEvent.change(screen.getByLabelText('Custom alias'), {
            target: { value: 'my-alias' },
        })
        fireEvent.click(screen.getByRole('button', { name: /shorten/i }))

        await waitFor(() => {
            expect(mockedApi.shorten).toHaveBeenCalledWith('https://example.com', 'my-alias')
        })
    })

    it('shows API error message on failure', async () => {
        mockedApi.shorten.mockRejectedValueOnce(new Error("Alias 'taken' is already taken"))
        render(<ShortenForm onSuccess={onSuccess} />)

        fireEvent.change(screen.getByLabelText('Full URL to shorten'), {
            target: { value: 'https://example.com' },
        })
        fireEvent.click(screen.getByRole('button', { name: /shorten/i }))

        await waitFor(() => {
            expect(screen.getByRole('alert')).toHaveTextContent("Alias 'taken' is already taken")
        })
        expect(onSuccess).not.toHaveBeenCalled()
    })

    it('triggers shorten on Enter key press', async () => {
        mockedApi.shorten.mockResolvedValueOnce({ shortUrl: 'http://localhost:8080/kbdtest' })
        render(<ShortenForm onSuccess={onSuccess} />)

        const input = screen.getByLabelText('Full URL to shorten')
        fireEvent.change(input, { target: { value: 'https://example.com' } })
        fireEvent.keyDown(input, { key: 'Enter' })

        await waitFor(() => {
            expect(mockedApi.shorten).toHaveBeenCalledOnce()
        })
    })
})

// ── UrlTable ──────────────────────────────────────────────────────────────────

describe('UrlTable', () => {
    const mockUrls = [
        { alias: 'abc', fullUrl: 'https://example.com', shortUrl: 'http://localhost:8080/abc' },
        { alias: 'xyz', fullUrl: 'https://other.com',   shortUrl: 'http://localhost:8080/xyz' },
    ]
    const onDelete = vi.fn()

    beforeEach(() => vi.clearAllMocks())

    it('renders nothing when urls list is empty', () => {
        const { container } = render(<UrlTable urls={[]} onDelete={onDelete} />)
        expect(container.firstChild).toBeNull()
    })

    it('renders all URL entries', () => {
        render(<UrlTable urls={mockUrls} onDelete={onDelete} />)
        expect(screen.getByText('/abc')).toBeInTheDocument()
        expect(screen.getByText('/xyz')).toBeInTheDocument()
        expect(screen.getByText('https://example.com')).toBeInTheDocument()
    })

    it('shows entry count', () => {
        render(<UrlTable urls={mockUrls} onDelete={onDelete} />)
        expect(screen.getByText('2 entries')).toBeInTheDocument()
    })

    it('calls onDelete with correct alias', async () => {
        onDelete.mockResolvedValueOnce(undefined)
        render(<UrlTable urls={mockUrls} onDelete={onDelete} />)

        fireEvent.click(screen.getByLabelText('Delete abc'))

        await waitFor(() => {
            expect(onDelete).toHaveBeenCalledWith('abc')
        })
    })
})