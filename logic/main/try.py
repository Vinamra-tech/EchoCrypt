from flask import Flask, request, jsonify
from flask_cors import CORS
import base64
import io
import qrcode

app = Flask(__name__)
CORS(app)

# --------- Substitution Cipher Logic ----------
def generate_key():
    import random
    printable_chars = [chr(i) for i in range(32, 127)]
    key = printable_chars.copy()
    random.shuffle(key)
    key=''.join(key)
    return key

def create_substitution_mapping(key):
    printable_chars = [chr(i) for i in range(32, 127)]
    substitution = {original: substituted for original, substituted in zip(printable_chars, key)}
    return substitution

def substitute_encrypt(plain_text, key,n=10):
    current_key = key
    cipher_text = plain_text
    substitution_history = []
        
    for _ in range(n):
            # Create substitution mapping for this round
        substitution = create_substitution_mapping(current_key)
        substitution_history.append(substitution)
            
            # Apply substitution to text
        cipher_text = ''.join([substitution.get(c, c) for c in cipher_text])
            
            # Transform the key for next round
        current_key = ''.join([substitution.get(c, c) for c in current_key])

        # Save encrypted file
    return cipher_text

def substitute_decrypt(cipher_text, key,n=10):
    current_key = key
    substitution_history = []
        
    for _ in range(n):
        substitution = create_substitution_mapping(current_key)
        substitution_history.append(substitution)
            # Transform the key the same way as during encryption
        current_key = ''.join([substitution.get(c, c) for c in current_key])

        # Apply reverse substitutions in reverse order
    plain_text = cipher_text
    for substitution in reversed(substitution_history):
        inverse_substitution = {v: k for k, v in substitution.items()}
        plain_text = ''.join([inverse_substitution.get(c, c) for c in plain_text])
    return plain_text

def generate_password(cipher_text):
    """Generate 8-digit password from cipher text using modulus cycling without repeating characters"""
    password_chars = []
    password2 = ""
    used_chars = set()  # Track used characters to avoid duplicates
    
    if len(cipher_text) > 0:
        index = 7 % len(cipher_text)  # Start at position 7 (or wrap around)
        while len(password_chars) < 8 and len(used_chars) < len(cipher_text):
            char = cipher_text[index]
            if char not in used_chars:
                password_chars.append(char)
                used_chars.add(char)
            index = (7 + index) % len(cipher_text)  # Next index = (7 + current) % length
    
    password = ''.join(password_chars)
    for i in password:
        if i == " ":
            password2 += "#"
        elif i=="\n":
            password2 += "@"
        elif i=="\t" :
            password2 += "!"
        else:
            password2 += i
    
    # If we didn't get 8 unique characters, pad with something (optional)
    while len(password2) < 8:
        password2 += "0"
    
    return password2[:8]  # Ensure exactly 8 characters

# --------- QR Code Utilities ----------
def generate_qr_code(key):
    qr = qrcode.QRCode(version=1, box_size=10, border=4)
    qr.add_data(key)
    qr.make(fit=True)
    img = qr.make_image(fill='black', back_color='white')
    buffered = io.BytesIO()
    img.save(buffered, format="PNG")
    return base64.b64encode(buffered.getvalue()).decode()

# --------- Flask Routes ----------

@app.route('/encrypt', methods=['POST'])
def encrypt_file():
    data = request.get_json()
    if not data or 'encrypted_file' not in data:
        return jsonify({"error": "No 'encrypted_file' data in JSON"}), 400

    try:
        file_base64 = data['encrypted_file']
        file_bytes = base64.b64decode(file_base64)
        plain_text = file_bytes.decode('utf-8')

        key = generate_key()
        encrypted_text = substitute_encrypt(plain_text, key)
        password = generate_password(encrypted_text)

        qr_base64 = generate_qr_code(key)
        encrypted_base64 = base64.b64encode(encrypted_text.encode()).decode()

        return jsonify({
            "encrypted": encrypted_base64,
            "qr": qr_base64,
            "password": password
        })

    except Exception as e:
        return jsonify({"stderr": str(e)}), 500

@app.route('/decrypt', methods=['POST'])
def decrypt_file():
    try:
        data = request.get_json()
        key = data.get("key")
        password = data.get("password")
        encrypted_file_b64 = data.get("encrypted_file")

        encrypted_text = base64.b64decode(encrypted_file_b64).decode()
        expected_password = generate_password(encrypted_text)

        if password != expected_password:
            return jsonify({"stderr": "Invalid password",
                            "stdout" :"",
                            "exitCode" :401,
                            "outputFile" :""})

        decrypted_text = substitute_decrypt(encrypted_text, key)
        decrypted_base64 = base64.b64encode(decrypted_text.encode()).decode()

        return jsonify({
            "original_file": decrypted_base64
        })

    except Exception as e:
        return jsonify({"stderr": str(e)}), 500

# --------- Run Server ----------
if __name__ == '__main__':
    app.run(debug=True)