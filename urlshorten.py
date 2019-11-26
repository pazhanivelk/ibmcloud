import requests
endpoint = "https://api-ssl.bitly.com/v4/shorten"
data = {
"domain": "bit.ly",
"long_url": "https://www.google.com"
}
headers = {"Authorization": "Bearer 0736d8604168df7b03b7fc21fe2293f6a5196688", "Content-Type":"application/json"}
print(requests.post(endpoint, data=data, headers=headers).json())
