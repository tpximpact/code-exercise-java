import { useEffect, useState } from "react";
import type TabProperties from "./TabProperties";

interface functionProperties {
    data: TabProperties[]
}

export default function Tab({ data }: functionProperties){
    const [selectedTab, setSelectedTab] = useState(data[0].id);

    useEffect(()=>{
        const id = window.location.hash.substring(1);

        if(data.find((tab)=>tab.id==id)){
            setSelectedTab(id);
        }
    }, []);

    return (
        <div className="govuk-tabs" data-module="govuk-tabs">
            <ul className="govuk-tabs__list">
                {data.map((tab)=>(
                    <li key={`tab-title-${tab.id}`} className={`govuk-tabs__list-item ${selectedTab == tab.id ? 'govuk-tabs__list-item--selected' : ''}`}>
                        <a className="govuk-tabs__tab" onClick={()=>setSelectedTab(tab.id)} href={`#${tab.id}`}>
                            {tab.title}
                        </a>
                    </li>
                ))}
            </ul>

            {data.map((tab)=>(
                <div key={`tab-content-${tab.id}`} className={`govuk-tabs__panel ${selectedTab == tab.id ? '' : 'govuk-tabs__panel--hidden'}`} id={tab.id}>
                    {tab.content}
                </div>
            ))}
        </div>
    );
} 