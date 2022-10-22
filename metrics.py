import matplotlib.pyplot as plt
import pandas as pd 

def LogMetrics():
    possibleInstructions = [
        f'{instructions} instruções' for instructions in range(1, 10)
    ]

    results = []

    possibleQuantums = list(range(1,40))
    possibleQuantums = [
        quantum if quantum >= 10 else f'0{str(quantum)}' for quantum in possibleQuantums
    ]

    for quantum in possibleQuantums:
        with open(f'resultados/log{quantum}.txt', 'r') as f:
            log = f.read()
            instructionCount = [log.count(instructions) for instructions in possibleInstructions]
            averageInstruction = sum([
                (instructionNumber+1)*instructionCount for instructionNumber,instructionCount in enumerate(instructionCount)
            ])/sum(instructionCount)
            print(f'Quantum {quantum}')
            print(f'Número de trocas: {sum(instructionCount)}')
            print(f'Número médio de instruções: {averageInstruction}')
            results.append([quantum, sum(instructionCount), averageInstruction])

    with open('resultados/relatorio.csv', 'w') as f:
        f.write('Quantum,Trocas,Intruções\n')
        for result in results:
            f.write(f'{result[0]},{result[1]},{result[2]}\n')

LogMetrics()
        
pd.read_csv('resultados/relatorio.csv').plot(x='Quantum', y=['Intruções', 'Trocas'])
plt.show()