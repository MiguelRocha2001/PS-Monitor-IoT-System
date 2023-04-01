import Alert from 'react-bootstrap/Alert';

export function Loading() {
    return (
        <Alert variant={'warning'}>
            <Alert.Heading>Loading...</Alert.Heading>
        </Alert>
    );
}