#!/bin/bash
# 将 <YourAppToken> 替换为你在服务端生成的 App Token，<YourPassword> 替换为你设置的新密码

client_id="YXA6NCizWK8ZTiKLcY-NJwAgbg"
client_secret="YXA6Dzj9HhxCT376B-IgZAU9ACrL8wA"
baseurl="https://a1.easemob.com/1135230423163966/demo"

getToken(){
   curl -X POST -H 'Content-Type: application/json' -H 'Accept: application/json' -d "{
   \"grant_type\": \"client_credentials\",
   \"client_id\": \"${client_id}\",
   \"client_secret\": \"${client_secret}\",
   \"ttl\": \"1024000\"
}" "${baseurl}/token"
}

updatePassword(){
   username="root"
   pswd="123"
   curl -X PUT -H 'Content-Type: application/json' \
      -H 'Accept: application/json' \
      -H "Authorization: Bearer $YourAppToken" \
      -d "{ \"newpassword\": \"${pswd}\" }" \
      "${baseurl}/users/${username}/password"
}

uploadFile(){
   local file="/Users/user/Downloads/iShot_2023-04-25_05.23.10.png"
   curl -X POST ${baseurl}/chatfiles \
      -H "Authorization: Bearer $YourAppToken" \
      -H "content-type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW" \
      -F file=@${file}
      #-H 'restrict-access: true' \
}
#{"path":"/chatfiles","uri":"https://a1.easemob.com/1135230423163966/demo/chatfiles","timestamp":1682372070664,"organization":"1135230423163966","application":"3428b358-af19-4e22-8b71-8f8d2700206e","entities":[{"uuid":"cc0696a0-e2e7-11ed-88b6-f98a29f3b73f","type":"chatfile"}],"action":"post","duration":28,"applicationName":"demo"}

getToken
#updatePassword
#uploadFile
