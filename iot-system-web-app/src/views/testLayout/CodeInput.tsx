import React, {useEffect, useRef, useState} from 'react';
import './CodeInput.css';


type CodeInputProps = {
    onCodeSubmit: (code: string) => boolean;
};



function InputCode({ onCodeSubmit }: CodeInputProps) {
    const [code, setCode] = useState<string>('');
    const[isCodeIncorrect, setIsCodeIncorrect] = useState<boolean>(false);
    const inputRef = useRef<HTMLInputElement>(null);
    const [isSubmitted, setIsSubmitted] = useState<boolean>(false);

    useEffect(() => {
        const handleClickOutside = (e: MouseEvent) => {
            if (inputRef.current && !inputRef.current.contains(e.target as Node)) {
                inputRef.current.focus();
            }
        };

        window.addEventListener('click', handleClickOutside);

        return () => {
            window.removeEventListener('click', handleClickOutside);
        };
    }, []);

    useEffect(() => {
        if (inputRef.current && code.length === 0) {
            inputRef.current.focus();
        }
    }, [code]);



    const handlePaste = (e: React.ClipboardEvent<HTMLInputElement>) => {
        const pastedCode = e.clipboardData.getData('text').replace(/[^a-zA-Z0-9]/g, '').toUpperCase();
        console.log(pastedCode)
        if (pastedCode.length == 5) {
            setCode(pastedCode);
        }
    };

    const handleCodeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
            const input = e.target.value;
            // only accept alphanumeric characters
            const regex = /^[a-zA-Z0-9]+$/;
            if (input.length <= 5 && regex.test(input)) {
                setCode(input.toUpperCase());
                setIsCodeIncorrect(false)
                if (input.length === 5) {
                    if(!isSubmitted) {
                        setIsSubmitted(true)
                        if (!onCodeSubmit(input.toUpperCase())) {
                        setCode('')
                        setIsCodeIncorrect(true)
                        console.log("Incorrect code", code)
                        setTimeout(() => {
                            setIsSubmitted(false);
                            setCode('')
                        }, 1000);
                    } else
                        console.log("Correct code")
                }
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
        <div className={`code-input${isCodeIncorrect ? ' shake red' : ''}`} onClick={handleInputClick}>
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
                ref={inputRef}
                onPaste={handlePaste}
            />
        </div>
    );
}

export default InputCode;
