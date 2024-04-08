from http.server import HTTPServer, BaseHTTPRequestHandler
import firebase_admin as fa
from firebase_admin import credentials, firestore
from urllib.parse import urlparse, parse_qs
import os
from Crypto.Cipher import AES
from Crypto.Util.Padding import pad
import json

#HTTP Requst Handler for the Python Web Server
class SimpleHTTPRequestHandler(BaseHTTPRequestHandler):
    #Initialize the Firebase App
    cred = credentials.Certificate("./vanklcomm-firebase-adminsdk-1aie5-6a096bebf8.json")
    fa.initialize_app(cred)
    db = firestore.client()

    #Update Key Store on First Creation of HTTP Request
    keyStore = {}
    if not keyStore:
        print("keystore Updated")
        doc_ref = db.collection("users")
        docs = doc_ref.stream()
        for doc in docs:
            docVal = doc.to_dict()
            if "Key" in docVal:
                keyStore[docVal["email"]] = docVal["Key"]

    #If Session is already Online
    alreadyOnline = {}

    #Initializing the Server
    def __init__(self, store, *args, **kwargs):
        super().__init__(*args, **kwargs)
        print("Initializing")

    #Specifies Get Request Functionality
    def do_GET(self):
        #Get the Parsed URL and Query Parameters
        parsed_url = urlparse(self.path)
        query_params = parse_qs(parsed_url.query)

        #If the Path is Deauthenticate
        if parsed_url.path == "/deauthenticate":
            print("Entered Deauthenticate")
            #Ensure that email in Query Parameters
            if 'email' in query_params:
                #Remove the Session
                del self.alreadyOnline[query_params['email'][0]]

                #Return a Response og the current online sessions
                json_result = json.dumps(self.alreadyOnline).encode('utf-8')
                print(json_result)
                self.send_response(200)
                self.send_header('Content-type', 'text/json')
                self.end_headers()
                self.wfile.write(json_result)
            #Return Failure response if no Email
            else:
                self.send_response(400)
                self.send_header('Content-type', 'text/html')
                self.end_headers()
                self.wfile.write(b"Missing parameters in the URL")
        #If parsed Path is Authenticate
        elif parsed_url.path == '/authenticate':
            print("Entered Authenticate")
            result = {}
            #Ensure all query Parameters are present
            if 'target' in query_params and 'email' in query_params and 'nonce' in query_params:
                #IF the target is already online
                if query_params['target'][0] in self.alreadyOnline:
                    #Return Result with exists and session key which already exists
                    result["header"] = "Exists"
                    result["session_key"]=self.alreadyOnline[query_params['target'][0]]
                    json_result = json.dumps(result).encode('utf-8')
                    print(json_result)

                    #Add current sender to sessions
                    self.alreadyOnline[query_params['email'][0]]=self.alreadyOnline[query_params['target'][0]]

                    #Return Result
                    self.send_response(200)
                    self.send_header('Content-type', 'text/json')
                    self.end_headers()
                    self.wfile.write(json_result)
                #If target is not already online
                else:
                    #Get keys from the Key Store
                    keyTotalEncrypt = self.keyStore[query_params['email'][0]]
                    keyContact = self.keyStore[query_params['target'][0]]

                    #Encrypt the nonce
                    nonceval = self.encrypt(keyTotalEncrypt,query_params['nonce'][0])
                    #Generate Session Key
                    sessionKey = os.urandom(16)

                    #Set Result Values
                    result["header"]="New"
                    result["nonce"] = nonceval.hex()
                    result['session_key'] = sessionKey.hex()
                    result["target"] = self.encrypt(keyTotalEncrypt, query_params['target'][0]).hex()
                    result["sender"] = self.encrypt(keyContact, query_params['email'][0] ).hex()

                    #Return the resulting JSON
                    print(json_result)
                    json_result = json.dumps(result).encode('utf-8')
                    self.alreadyOnline[query_params['email'][0]]=sessionKey.hex()
                    self.send_response(200)
                    self.send_header('Content-type', 'text/json')
                    self.end_headers()
                    self.wfile.write(json_result)
            #Missing Parameters return failure
            else:
                self.send_response(400)
                self.send_header('Content-type', 'text/html')
                self.end_headers()
                self.wfile.write(b"Missing parameters in the URL")
        #Update db to include the User Key
        elif parsed_url.path == '/updatedb':
            #Ensure Query Parameters Exist or return failure
            if 'email' in query_params:
                #Extract the email parameter value
                user_email = query_params['email'][0]
                self.updateDB(user_email)
            else:
                self.send_response(400)
                self.send_header('Content-type', 'text/html')
                self.end_headers()
                self.wfile.write(b"Missing 'email' parameter in the URL")
        #Return All User and Key in Key Store
        elif parsed_url.path == '/getkeys':
            print("In Keys")
            response_message = "Key Store: \n"
            for key, value in self.keyStore.items():
                response_message += key + " + " + str(value) + "\n"
            self.send_response(200)
            self.send_header('Content-type', 'text/html')
            self.end_headers()
            self.wfile.write(response_message.encode('utf-8'))
        #Return if none of the paths are valid
        else:
            self.send_response(400)
            self.send_header('Content-type', 'text/html')
            self.end_headers()
            self.wfile.write(b"Not Valid Path")

    #Ensure Correct Padding to encrypt
    def pad(self, text):
        # Convert string to bytes
        n = len(text) % 8
        return text + (b' ' * n)

    #Convert a string to bytes for encryption
    def string_to_bytes(self, text):
        return bytes(text, 'utf-8')

    #AES Encryption of text based on key
    def encrypt(self, key, text):
        aes = AES.new(key,AES.MODE_ECB)
        padTxt = pad(self.string_to_bytes(text), 16)
        encrypted_text = aes.encrypt(padTxt)
        return encrypted_text

    #AES Decryption of text based on key
    def decrypt(self, key, text):
        aes = AES.new(key,AES.MODE_ECB)
        encrypted_text = aes.decrypt(text)
        return encrypted_text

    #Update the Database to include the Key
    def updateDB(self, email):
        #Generate a Random Key
        rand_key = os.urandom(16);

        #Get Document for user based on email
        doc_ref = self.db.collection("users").where('email', '==', email).limit(1).get()
        if doc_ref:
            # Iterate over the query results (there should be only one document)
            for doc in doc_ref:
                # Set the data for the found document
                doc.reference.set({"Key": rand_key}, merge=True)
                self.keyStore[email] = rand_key

                #Return Response that keystore updated
                response_message = "KeyStore Updated"
                self.send_response(200)
                self.send_header('Content-type', 'text/html')
                self.end_headers()
                self.wfile.write(response_message.encode('utf-8'))

#Start running the server
def run(server_class=HTTPServer, handler_class=SimpleHTTPRequestHandler, port=8000):
    server_address = ('', port)
    keyStore = {}
    httpd = server_class(server_address, lambda *args, **kwargs: handler_class(keyStore, *args, **kwargs))
    print(f"Server running on port {port}")
    httpd.serve_forever()

#Init Server
if __name__ == "__main__":
    run()
