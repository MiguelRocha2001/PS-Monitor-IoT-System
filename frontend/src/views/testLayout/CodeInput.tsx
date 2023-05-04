import React, { useState } from 'react';
import './CodeInput.css';


type CodeInputProps = {
    onCodeSubmit: (code: string) => boolean;
};

const onCodeSubmit = (password: string) => {
    return password == "ABCDE";
}

const InputCode = () => {
    const [code, setCode] = useState<string>('');
    const[isCodeIncorrect, setIsCodeIncorrect] = useState<boolean>(false);

    const handleCodeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const input = e.target.value;
        // only accept alphanumeric characters
        const regex = /^[a-zA-Z0-9]+$/;
        if (input.length <= 5 && regex.test(input)) {
            setCode(input.toUpperCase());
            setIsCodeIncorrect(false)
            if (input.length === 5) {
                if(!onCodeSubmit(input.toUpperCase())) {
                    setCode('')
                    setIsCodeIncorrect(true)
                    console.log("Incorrect code")
                }
                else
                    console.log("Correct code")
            }
        }
    };

    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === 'Backspace' && code.length > 0) {
            setCode(code.slice(0, -1));
        }
    };

    const handleInputClick = () => {
        const input = document.getElementById('code-input');
        if (input) {
            input.focus();
        }
    };

    return (
        <div className={`code-input${isCodeIncorrect ? ' shake' : ''}`} onClick={handleInputClick}>
            {[...Array(5)].map((_, index) => (
                <div
                    key={index}
                    className={`code-input-square${index === code.length ? ' active' : ''}`}
                >
                    {code[index]}
                </div>
            ))}
            <input
                id="code-input"
                type="text"
                value={code}
                onChange={handleCodeChange}
                onKeyDown={handleKeyDown}
                maxLength={5}
                autoComplete="off"
                tabIndex={-1}
            />
        </div>
    );
};

export default InputCode;
