import { useEffect, useState, type JSX } from "react";
import type TabProperties from "../../components/tabs/TabProperties";
import Tab from "../../components/tabs/Tab";
import ShortenUrl from "./shorten-url/ShortenUrl";
import UrlList from "./url-list/url-list";

export function FrontPage() {
    const API_BASE = "http://localhost:8080";

    const [fullUrl, setFullUrl] = useState("");
    const [customAlias, setCustomAlias] = useState("");
    const [urls, setUrls] = useState([]);
    const [shortUrl, setShortUrl] = useState("");
    const [error, setError] = useState("");
    const [selectedTab, setSelectedTab] = useState(0);

    const [newUrlLink, setNewUrlLink] = useState("");

    const fetchUrls = async () => {
        try {
            const response = await fetch(`${API_BASE}/urls`);
            const data = await response.json();
            setUrls(data);
        } catch (err) {
            console.error(err);
        }
    };

    useEffect(() => {
        fetchUrls();
    }, []);


    const tabInformation: TabProperties[] = [
        {
            id: 'shorten-url',
            title: 'Shorten A New Url',
            content: <ShortenUrl setNewUrlLink={setNewUrlLink} />
        },
        {
            id: 'url-list',
            title: 'Url List',
            content: <UrlList />
        }
    ]

    return (
        <div className="govuk-width-container">
            <main className="govuk-main-wrapper" id="main-content">
                {
                    newUrlLink && (
                        <div className="govuk-notification-banner govuk-notification-banner--success" role="alert" aria-labelledby="govuk-notification-banner-title" data-module="govuk-notification-banner">
                            <div className="govuk-notification-banner__header">
                                <h2 className="govuk-notification-banner__title" id="govuk-notification-banner-title">
                                    Success
                                </h2>
                            </div>
                            <div className="govuk-notification-banner__content">
                                <h3 className="govuk-notification-banner__heading">
                                    Shortened Url has been created
                                </h3>
                                <p className="govuk-body">Visit <a className="govuk-notification-banner__link" href={newUrlLink}>{newUrlLink}</a> to be redirected to your chosen url</p>
                            </div>
                        </div>
                    )
                }


                <h1 className="govuk-heading-l">URL Shortener</h1>
                
                <Tab data={tabInformation} />
            </main>
        </div>
    );
}
