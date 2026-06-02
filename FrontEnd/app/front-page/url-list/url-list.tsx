import { useEffect, useState } from "react";
import type { urlEntry } from "../types";

interface componentProps{
    urls: urlEntry[],
    deleteLink: (url: urlEntry) => void
}

export default function UrlList({urls, deleteLink}: componentProps){
    const API_BASE = "http://localhost:8080";
    
    return urls.length == 0 
        ? (
            <div className="govuk-warning-text">
                <span className="govuk-warning-text__icon" aria-hidden="true">!</span>
                <strong className="govuk-warning-text__text">
                    <span className="govuk-visually-hidden">Warning</span>
                    No urls can be found!
                </strong>
            </div>
        ) 
        : (
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
                    {urls.map((url: urlEntry)=>(
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