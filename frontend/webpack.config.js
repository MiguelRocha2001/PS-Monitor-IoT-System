const HtmlWebpackPlugin = require('html-webpack-plugin');
const { EnvironmentPlugin } = require('webpack');

module.exports = (env, argv) => {
    const isDevelopment = argv.mode === 'development';
    const targetPort = isDevelopment ? 8080 : 9000;
    return {
        mode: "development",
        resolve: {
            extensions: [".js", ".ts", ".tsx"]
        },
        devServer: {
            historyApiFallback: true,
            proxy: {
                '/api': {
                  secure: false,//TO USE SELF SIGNED CERTIFICATE
                  target: 'http://localhost:9000',
                },
              },
        },
        module: {
            rules: [
                {
                    test: /\.tsx?$/,
                    use: 'ts-loader',
                    exclude: /node_modules/
                },
                {
                    test: /\.css$/,
                    use: [
                        'style-loader',
                        'css-loader'
                    ]
                }
            ]
        },

        plugins: [
            new HtmlWebpackPlugin({
                template : './public/index.html'
            }),
            new EnvironmentPlugin({
                TARGET_PORT: targetPort.toString()
            })
        ]
    }
}

