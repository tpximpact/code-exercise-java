import { useEffect, useState } from "react";

interface componentProps{
    setNewUrlLink: React.Dispatch<React.SetStateAction<string>>
}

export default function ShortenUrl(props: componentProps){
    const API_BASE = "http://localhost:8080";

    const [aliasError, setAliasError] = useState("");
    const [urlError, setUrlError] = useState("");

    //form fields
    const [url, setUrl] = useState("");
    const [alias, setAlias] = useState("");

    const shortenUrl = async () => {
        setAliasError("");
        setUrlError("");

        try {
            const response = await fetch(`${API_BASE}/shorten`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    fullUrl: url,
                    customAlias: alias || undefined,
                }),
            });

            if (response.status == 400) {
                const text = await response.text();

                if(text == "Invalid input"){
                    setUrlError("Please enter a valid url!");
                } else {
                    setAliasError("Please enter a unique alias, you can check the Url list to see which ones are currently in use");
                }
                throw new Error(text);
            }

            const data = await response.json();

            props.setNewUrlLink(data.shortUrl);
            setUrl("");
            setAlias("");
        } catch (err: any) {
                console.log('ERROR: ', err);
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        await shortenUrl();
    }

    return (
        <form onSubmit={handleSubmit}>
            <div className={`govuk-form-group ${urlError && 'govuk-form-group--error'}`}>
                <h2 className="govuk-label-wrapper">
                    <label className="govuk-label govuk-label--l" htmlFor="shorten-input">
                        Enter the full url that you would like shortened
                    </label>
                </h2>
                {urlError && (
                    <p id="event-name-error" className="govuk-error-message">
                        <span className="govuk-visually-hidden">Error:</span> {urlError}
                    </p>
                )}
                <div id="shorten-input-hint" className="govuk-hint">
                    Ensure that the URL is valid, once submitted you will be provided with the shortened link
                </div>
                <input
                    className={`govuk-input govuk-input--width-20 ${urlError && 'govuk-input--error'}`}
                    id="shorten-input"
                    name="url"
                    type="url"
                    aria-describedby="shorten-input-hint"
                    value={url}
                    onChange={(e) => setUrl(e.target.value)}
                />           
            </div>

            <details className="govuk-details">
                <summary className="govuk-details__summary">
                    <span className="govuk-details__summary-text">
                        Choose a custom alias
                    </span>
                </summary>
                <div className="govuk-details__text">
                    <div className={`govuk-form-group ${aliasError && 'govuk-form-group--error'}`}>
                        <label className="govuk-label" htmlFor="event-name">
                            Insert custom alias text
                        </label>

                        {aliasError && (
                            <p id="event-name-error" className="govuk-error-message">
                                <span className="govuk-visually-hidden">Error:</span> {aliasError}
                            </p>
                        )}

                        <input 
                            className={`govuk-input govuk-input--width-20 ${aliasError && 'govuk-input--error'}`}
                            id="custom-alias"
                            name="customAlias"
                            type="text"
                            value={alias}
                            onChange={(event) => setAlias(event.target.value)}
                        />            
                    </div>
                </div>
            </details>

            <button type="submit" data-prevent-double-click="true" className="govuk-button" data-module="govuk-button">
                Confirm
            </button>
            <button onClick={()=>{setAlias(""); setUrl("");}} style={{marginLeft: '1rem'}} className="govuk-button govuk-button--secondary" data-module="govuk-button">
                Reset
            </button>
        </form>
    );
}