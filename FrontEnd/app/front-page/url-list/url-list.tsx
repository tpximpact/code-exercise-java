import { useEffect, useState } from "react";

interface urlEntry{ 
    alias: string, 
    fullUrl: string, 
    shortUrl: string 
}

export default function UrlList(){
    const API_BASE = "http://localhost:8080";
    const [urls, setUrls] = useState<urlEntry[]>([]);
    
    const fetchUrls = async () => {
        try {
            const response = await fetch(`${API_BASE}/urls`);
            const data = await response.json() as urlEntry[];
            setUrls(data);
        } catch (err) {
            console.error(err);
        }
    };

    const deleteLink = async (url: urlEntry) => {
        await fetch(`${API_BASE}/${encodeURIComponent(url.alias)}`, { method: "DELETE" });

        await fetchUrls();
    };

    useEffect(() => {
        fetchUrls();
    }, []);
    
    return (
        <table className="govuk-table govuk-table--small-text-until-tablet">
            <caption className="govuk-table__caption govuk-table__caption--m">Current Url List</caption>
            <thead className="govuk-table__head">
                <tr className="govuk-table__row">
                    <th scope="col" className="govuk-table__header">Full Url</th>
                    <th scope="col" className="govuk-table__header">Shortened Url</th>
                    <th scope="col" className="govuk-table__header"></th>
                </tr>
            </thead>
            <tbody className="govuk-table__body">
                {urls.map((url)=>(
                    <tr className="govuk-table__row">
                        <th scope="row" className="govuk-table__header">
                            {url.fullUrl}
                        </th>
                        <td className="govuk-table__cell">
                            <a href={url.shortUrl} className="govuk-link">
                                {url.shortUrl}
                            </a>
                        </td>
                        <td className="govuk-table__cell">
                            <button onClick={()=>{deleteLink(url)}} className="govuk-button govuk-button--warning" data-module="govuk-button">
                                Delete url
                            </button>
                        </td>
                    </tr>
                ))}

                
            </tbody>
        </table>
    );
}