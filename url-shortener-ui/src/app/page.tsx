import UrlShortenerApp from "@/app/UrlShortenerApp";

export default function Home() {
  return (
    <div>
        <div className="min-h-screen flex flex-col">
            {/* Header */}
            <header className="bg-ink text-paper border-b-2 border-ink">
                <div className="max-w-2xl mx-auto px-6 py-5 flex items-baseline gap-5">
                    <div className="flex items-center gap-2">
                        <span className="text-accent text-2xl leading-none select-none">⌗</span>
                        <span className="font-display font-extrabold text-[2rem] leading-none tracking-[-0.04em] text-paper">
              snip
            </span>
                    </div>
                    <p className="font-mono text-[0.68rem] tracking-widest uppercase text-paper/40 hidden sm:block">
                        Trim the excess. Keep the intent.
                    </p>
                </div>
            </header>

            {/* Main */}
            <main className="flex-1 max-w-2xl w-full mx-auto px-6 py-10 flex flex-col gap-8">
                <UrlShortenerApp />
            </main>

            {/* Footer */}
            <footer className="border-t-2 border-border">
                <div className="max-w-2xl mx-auto px-6 py-4 flex items-center justify-between">
          <span className="font-mono text-[0.65rem] text-muted uppercase tracking-wider">
            snip / url shortener
          </span>
                    <span className="font-mono text-[0.65rem] text-muted">
            Next.js · Spring Boot · PostgreSQL
          </span>
                </div>
            </footer>
        </div>
    </div>
  );
}
