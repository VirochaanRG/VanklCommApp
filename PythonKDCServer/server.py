from http.server import HTTPServer, BaseHTTPRequestHandler
import firebase_admin as fa
from firebase_admin import credentials, firestore
from urllib.parse import urlparse, parse_qs
import os
import binascii
from Crypto.Cipher import AES
from Crypto.Util.Padding import pad
import json

class SimpleHTTPRequestHandler(BaseHTTPRequestHandler):
    get_request_count = 0
    
    cred = credentials.Certificate("./vanklcomm-firebase-adminsdk-1aie5-6a096bebf8.json")
    fa.initialize_app(cred)
    db = firestore.client()
    keyStore = {}
    if not keyStore:
        print("keystore Updated")
        doc_ref = db.collection("users")
        docs = doc_ref.stream()
        for doc in docs:
            docVal = doc.to_dict()
            if "Key" in docVal:
                keyStore[docVal["email"]] = docVal["Key"]

    alreadyOnline = {}

    def __init__(self, store, *args, **kwargs):
        super().__init__(*args, **kwargs)
        print("Initializing")



    def do_GET(self):
        parsed_url = urlparse(self.path)
        query_params = parse_qs(parsed_url.query)
        print(parsed_url)
        print(query_params)
        if parsed_url.path == '/test':
            if 'email' in query_params:
                email = query_params['email'][0]
                doc_ref = self.db.collection("users").where('email', '==', email).limit(1).get()
                for doc in doc_ref:
                    user_data = doc.to_dict()
                print(user_data["Key"] == self.keyStore[query_params['email'][0]])
                print(user_data["Key"])
                print(self.keyStore[query_params['email'][0]])
                response_message = "Test"
                self.send_response(200)
                self.send_header('Content-type', 'text/html')
                self.end_headers()
                self.wfile.write(response_message.encode('utf-8'))
        elif parsed_url.path == "/deauthenticate":
            print("Entered Deauthenticate")
            if 'email' in query_params:
                del self.alreadyOnline[query_params['email'][0]]
                json_result = json.dumps(self.alreadyOnline).encode('utf-8')
                print(json_result)
                self.send_response(200)
                self.send_header('Content-type', 'text/json')
                self.end_headers()
                self.wfile.write(json_result)  
        elif parsed_url.path == '/authenticate':
            print("Entered Authenticate")
            result = {}
            if 'target' in query_params and 'email' in query_params and 'nonce' in query_params:
                if query_params['target'][0] in self.alreadyOnline:
                    result["header"] = "Exists"
                    result["session_key"]=self.alreadyOnline[query_params['target'][0]]
                    json_result = json.dumps(result).encode('utf-8')
                    print(json_result)

                    self.alreadyOnline[query_params['email'][0]]=self.alreadyOnline[query_params['target'][0]]

                    self.send_response(200)
                    self.send_header('Content-type', 'text/json')
                    self.end_headers()
                    self.wfile.write(json_result)
                else:
                    keyTotalEncrypt = self.keyStore[query_params['email'][0]]
                    keyContact = self.keyStore[query_params['target'][0]]
                    nonceval = self.encrypt(keyTotalEncrypt,query_params['nonce'][0])

                    result["header"]="New"
                    result["nonce"] = nonceval.hex()                    
                    sessionKey = os.urandom(16)
                    result['session_key'] = sessionKey.hex()
                    result["target"] = self.encrypt(keyTotalEncrypt, query_params['target'][0]).hex()
                    result["sender"] = self.encrypt(keyContact, query_params['email'][0] ).hex()
                    json_result = json.dumps(result).encode('utf-8')
                    print(json_result)
                    self.alreadyOnline[query_params['email'][0]]=sessionKey.hex()
                    self.send_response(200)
                    self.send_header('Content-type', 'text/json')
                    self.end_headers()
                    self.wfile.write(json_result)

        elif parsed_url.path == '/updatedb':
            if 'email' in query_params:
              # Extract the email parameter value
                user_email = query_params['email'][0]
                self.updateDB(user_email)
            else:
                self.send_response(400)
                self.send_header('Content-type', 'text/html')
                self.end_headers()
                self.wfile.write(b"Missing 'email' parameter in the URL")
        elif parsed_url.path == '/getkeys':
            print("In Keys")
            response_message = "Key Store: \n"
            print(self.keyStore)
            for key, value in self.keyStore.items():
                response_message += key + " + " + str(value) + "\n"
            self.send_response(200)
            self.send_header('Content-type', 'text/html')
            self.end_headers()
            self.wfile.write(response_message.encode('utf-8'))
        else:
            self.send_response(400)
            self.send_header('Content-type', 'text/html')
            self.end_headers()
            self.wfile.write(b"Missing 'email' parameter in the URL")
    
    def pad(self, text):
        # Convert string to bytes
        n = len(text) % 8
        return text + (b' ' * n)
    
    def string_to_bytes(self, text):
      return bytes(text, 'utf-8')
    
    def encrypt(self, key, text):
        aes = AES.new(key,AES.MODE_ECB)
        padTxt = pad(self.string_to_bytes(text), 16)
        encrypted_text = aes.encrypt(padTxt)
        return encrypted_text
    
    def decrypt(self, key, text):
        aes = AES.new(key,AES.MODE_ECB)
        encrypted_text = aes.decrypt(text)
        return encrypted_text
    def updateDB(self, email):
        rand_key = os.urandom(16);
        doc_ref = self.db.collection("users").where('email', '==', email).limit(1).get()
        if doc_ref:
          # Iterate over the query results (there should be only one document)
            for doc in doc_ref:
                # Set the data for the found document
                doc.reference.set({"Key": rand_key}, merge=True)
                self.keyStore[email] = rand_key 

                response_message = "KeyStore Updated"
                self.send_response(200)
                self.send_header('Content-type', 'text/html')
                self.end_headers()
                self.wfile.write(response_message.encode('utf-8'))

def run(server_class=HTTPServer, handler_class=SimpleHTTPRequestHandler, port=8000):
    server_address = ('', port)
    keyStore = {}
    httpd = server_class(server_address, lambda *args, **kwargs: handler_class(keyStore, *args, **kwargs))
    print(f"Server running on port {port}")
    httpd.serve_forever()


if __name__ == "__main__":
    run()
