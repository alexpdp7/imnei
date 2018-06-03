from setuptools import setup, find_packages

setup(name='imnei_pyclient',
      packages=find_packages(),
      install_requires=['grpcio-tools',],
      extras_require={
        'dev': ['ipython', 'ipdb',],
      },
      python_requires='>=3',
)
